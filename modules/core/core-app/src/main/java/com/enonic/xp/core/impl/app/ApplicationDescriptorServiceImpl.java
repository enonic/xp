package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateOrUpdateApplicationDescriptorParams;
import com.enonic.xp.app.EditableApplicationDescriptor;
import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.resource.MultiResourceProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

/**
 * Serves the descriptor of an application as a value derived from its descriptor and icon resources, cached by the
 * {@link ResourceService} like any other: stamped with the version of the application node, which every write of the persisted
 * descriptor or icon moves, so a change written on any node of the cluster is seen by the next read, and never cached for an
 * application in development, whose files change under it.
 */
@Component(immediate = true)
public class ApplicationDescriptorServiceImpl
    implements ApplicationDescriptorService
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationDescriptorServiceImpl.class );

    static final int MAX_ICON_SIZE = 100 * 1024;

    private static final String SEGMENT = ApplicationDescriptor.class.getName();

    private final ResourceService resourceService;

    private final ApplicationFactoryService applicationFactoryService;

    private final ApplicationRepoService repoService;

    private final DynamicSchemaAuditLogSupport auditLogSupport;

    private final EventPublisher eventPublisher;

    @Activate
    public ApplicationDescriptorServiceImpl( @Reference final ResourceService resourceService,
                                             @Reference final ApplicationFactoryService applicationFactoryService,
                                             @Reference final ApplicationRepoService repoService,
                                             @Reference final DynamicSchemaAuditLogSupport auditLogSupport,
                                             @Reference final EventPublisher eventPublisher )
    {
        this.resourceService = resourceService;
        this.applicationFactoryService = applicationFactoryService;
        this.repoService = repoService;
        this.auditLogSupport = auditLogSupport;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ApplicationDescriptor get( final @NonNull ApplicationKey key )
    {
        if ( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( key ) )
        {
            return null;
        }
        final LoadedDescriptor loaded = this.resourceService.processResources( processor( key ) );
        if ( loaded == null )
        {
            return null;
        }
        if ( loaded.fromResource() || this.applicationFactoryService.findResourceSource( key, null ).resolver() != null )
        {
            return loaded.descriptor();
        }
        return null;
    }

    // the descriptor paths are fixed, in order of preference: which of them the application serves is decided under the stamp
    private MultiResourceProcessor<ApplicationKey, LoadedDescriptor> processor( final ApplicationKey key )
    {
        return new MultiResourceProcessor.Builder<ApplicationKey, LoadedDescriptor>().key( key )
            .segment( SEGMENT )
            .keysTranslator( k -> ApplicationBundleUtils.DESCRIPTOR_PATHS.stream()
                .map( path -> ResourceKey.from( k, "/" + path ) )
                .toList() )
            .processor( resources -> load( key, resources ) )
            .build();
    }

    // a descriptor that cannot be built (a malformed yaml, an unreadable icon) is logged and the application has none, as before.
    // The failure is not cached: a read failing for a while (the binary of the icon out of reach) is retried on the next call,
    // and nothing tells a failure of that kind from a malformed descriptor, which is logged on every read until it is fixed
    private @Nullable LoadedDescriptor load( final ApplicationKey key, final List<Resource> candidates )
    {
        final Resource descriptor = candidates.stream().filter( Resource::exists ).findFirst().orElse( null );
        try
        {
            return descriptor != null
                ? new LoadedDescriptor( ApplicationDescriptorBuilder.build( key, descriptor, this.resourceService ), true )
                : new LoadedDescriptor( ApplicationDescriptorBuilder.build( key, this.resourceService ), false );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Unable to load application descriptor for {}", key, e );
            return null;
        }
    }

    @Override
    public @NonNull ApplicationDescriptor createOrUpdate( final @NonNull CreateOrUpdateApplicationDescriptorParams params )
    {
        ApplicationHelper.requireSchemaAdminRole();

        final ApplicationKey key = params.getKey();
        final Optional<Bundle> bundle = this.applicationFactoryService.findBundle( key );
        ApplicationHelper.requireNonSystemApplication( key, bundle.filter( ApplicationBundleUtils::isSystemApplication ).isPresent() );
        ApplicationHelper.requireNonLocalApplication( key, bundle.filter( ApplicationHelper::isLocalApplication ).isPresent() );

        // the application nodes live in the application repository, whatever the context of the caller
        final boolean created = ApplicationHelper.runAsAdmin( () -> this.repoService.getApplicationNode( key ) ) == null;
        if ( created )
        {
            this.repoService.createApplicationNode( key );
        }

        // the descriptor is edited and read back from the persisted source the write goes to, whatever resource source the
        // context of the caller selects
        final ApplicationDescriptor current = ApplicationHelper.callWithContext( () -> get( key ) );
        final ApplicationDescriptor source = current != null ? current : ApplicationDescriptor.create().key( key ).build();

        final EditableApplicationDescriptor edit = new EditableApplicationDescriptor( source );
        params.getEditor().edit( edit );

        final IconUpdate iconUpdate = iconUpdate( source.getIcon(), edit.icon );
        final String yaml = YmlApplicationDescriptorSerializer.serialize( edit.build() );

        this.repoService.upsertApplicationDescriptor( key, yaml, iconUpdate );

        // the write moved the version of the application node: the descriptor is read back the way every reader sees it from now on
        final ApplicationDescriptor result = Objects.requireNonNull( ApplicationHelper.callWithContext( () -> get( key ) ),
                                                                     "application descriptor not found after write" );

        final String iconMimeType = iconUpdate instanceof IconUpdate.Replace replace ? replace.mimeType() : null;
        final long iconSize = iconMimeType != null ? edit.icon.getSize() : 0;
        if ( created || current == null )
        {
            this.eventPublisher.publish( ApplicationEvents.installed( key ) );
            this.auditLogSupport.createApplicationDescriptor( key, yaml, iconMimeType, iconSize );
        }
        else
        {
            this.auditLogSupport.updateApplicationDescriptor( key, yaml, iconMimeType, iconSize,
                                                              iconUpdate instanceof IconUpdate.Remove );
            this.eventPublisher.publish(
                ApplicationEvents.updated( key, ApplicationEvents.KIND_APPLICATION, null, ApplicationEvents.ACTION_UPDATED ) );
        }
        return result;
    }

    /**
     * The icon as edited: the icon of the source left as is keeps the persisted icon, {@code null} removes it, anything else replaces it.
     */
    private static IconUpdate iconUpdate( final Icon sourceIcon, final Icon editedIcon )
    {
        if ( Objects.equals( sourceIcon, editedIcon ) )
        {
            return IconUpdate.KEEP;
        }
        if ( editedIcon == null )
        {
            return sourceIcon != null ? IconUpdate.REMOVE : IconUpdate.KEEP;
        }

        final String mimeType = editedIcon.getMimeType();
        if ( SchemaResourcePaths.appIconName( mimeType ) == null )
        {
            throw new IllegalArgumentException( String.format( "unsupported icon mime type: %s", mimeType ) );
        }
        final int size = editedIcon.getSize();
        if ( size <= 0 )
        {
            throw new IllegalArgumentException( "icon is empty" );
        }
        if ( size > MAX_ICON_SIZE )
        {
            throw new IllegalArgumentException( String.format( "icon size %d exceeds the limit of %d bytes", size, MAX_ICON_SIZE ) );
        }
        return IconUpdate.replace( ByteSource.wrap( editedIcon.toByteArray() ), mimeType );
    }

    /**
     * The descriptor as cached, and whether it was read from a descriptor resource: an application serving none still has a
     * descriptor, holding its key and icon, an unknown application has none.
     */
    private record LoadedDescriptor(ApplicationDescriptor descriptor, boolean fromResource)
    {
    }
}
