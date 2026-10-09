package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.ComponentContext;
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
import com.enonic.xp.core.internal.ApplicationBundleUtils;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.SystemConstants;

@Component(immediate = true)
public class ApplicationDescriptorServiceImpl
    implements ApplicationDescriptorService, SynchronousBundleListener, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplicationDescriptorServiceImpl.class );

    static final int MAX_ICON_SIZE = 100 * 1024;

    private static final String NODES_FIELD = "nodes";

    // the application node or its descriptor node: a change to either changes the descriptor read for the application
    private static final Pattern DESCRIPTOR_NODE_PATH = Pattern.compile(
        "^" + ApplicationRepoServiceImpl.APPLICATION_PATH + "/(?<name>[^/]+)(?:/" +
            Pattern.quote( SchemaResourcePaths.APP_DESCRIPTOR_NAME ) + ")?$" );

    private final ConcurrentMap<ApplicationKey, ApplicationDescriptor> appDescriptorMap;

    // the bundles of the installed applications: a descriptor is built from the bundle's resolver, which serves the persisted
    // descriptor first, or from the persisted nodes alone when the application has no bundle
    private final ConcurrentMap<ApplicationKey, Bundle> bundles;

    private final NodeService nodeService;

    private final ApplicationRepoService repoService;

    private final DynamicSchemaAuditLogSupport auditLogSupport;

    private final ApplicationFactory factory;

    @Activate
    public ApplicationDescriptorServiceImpl( @Reference final NodeService nodeService, @Reference final ApplicationRepoService repoService,
                                             @Reference final DynamicSchemaAuditLogSupport auditLogSupport )
    {
        this.appDescriptorMap = new ConcurrentHashMap<>();
        this.bundles = new ConcurrentHashMap<>();
        this.nodeService = nodeService;
        this.repoService = repoService;
        this.auditLogSupport = auditLogSupport;
        this.factory = new ApplicationFactory( nodeService );
    }

    @Override
    public ApplicationDescriptor get( final @NonNull ApplicationKey key )
    {
        final ApplicationDescriptor cached = this.appDescriptorMap.get( key );
        if ( cached != null )
        {
            return cached;
        }

        final ApplicationDescriptor loaded = loadDescriptor( key );
        if ( loaded == null )
        {
            return null;
        }
        final ApplicationDescriptor raced = this.appDescriptorMap.putIfAbsent( key, loaded );
        return raced != null ? raced : loaded;
    }

    private ApplicationDescriptor loadDescriptor( final ApplicationKey key )
    {
        final Bundle bundle = this.bundles.get( key );
        if ( bundle != null )
        {
            return ApplicationDescriptorBuilder.build( key, factory.createUrlResolver( bundle, null ) );
        }
        if ( this.repoService.getApplicationDescriptorNode( key ) != null )
        {
            return ApplicationDescriptorBuilder.build( key, ApplicationFactory.createPersistedSchemaResolver( key, nodeService ) );
        }
        return null;
    }

    @Override
    public @NonNull ApplicationDescriptor createOrUpdate( final @NonNull CreateOrUpdateApplicationDescriptorParams params )
    {
        ApplicationHelper.requireSchemaAdminRole();

        final ApplicationKey key = params.getKey();

        // the application nodes live in system-repo, whatever the context of the caller
        final boolean created = ApplicationHelper.runAsAdmin( () -> this.repoService.getApplicationNode( key ) ) == null;
        if ( created )
        {
            this.repoService.createApplicationNode( key );
        }

        final ApplicationDescriptor current = get( key );
        final ApplicationDescriptor source = current != null ? current : ApplicationDescriptor.create().key( key ).build();

        final EditableApplicationDescriptor edit = new EditableApplicationDescriptor( source );
        params.getEditor().edit( edit );

        final ApplicationIconUpdate iconUpdate = iconUpdate( source.getIcon(), edit.icon );
        final String yaml = YmlApplicationDescriptorSerializer.serialize( edit.build() );

        this.repoService.upsertApplicationDescriptor( key, yaml, iconUpdate );

        // the descriptor is read back the way every reader sees it, from this point on
        this.appDescriptorMap.remove( key );
        final ApplicationDescriptor result = Objects.requireNonNull( get( key ), "application descriptor not found after write" );

        final String iconMimeType = iconUpdate instanceof ApplicationIconUpdate.Replace replace ? replace.mimeType() : null;
        final long iconSize = iconMimeType != null ? edit.icon.getSize() : 0;
        if ( created || current == null )
        {
            this.auditLogSupport.createApplicationDescriptor( key, yaml, iconMimeType, iconSize );
        }
        else
        {
            this.auditLogSupport.updateApplicationDescriptor( key, yaml, iconMimeType, iconSize,
                                                              iconUpdate instanceof ApplicationIconUpdate.Remove );
        }
        return result;
    }

    /**
     * The icon as edited: the icon of the source left as is keeps the persisted icon, {@code null} removes it, anything else replaces it.
     */
    private static ApplicationIconUpdate iconUpdate( final Icon sourceIcon, final Icon editedIcon )
    {
        if ( Objects.equals( sourceIcon, editedIcon ) )
        {
            return ApplicationIconUpdate.KEEP;
        }
        if ( editedIcon == null )
        {
            return sourceIcon != null ? ApplicationIconUpdate.REMOVE : ApplicationIconUpdate.KEEP;
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
        return ApplicationIconUpdate.replace( ByteSource.wrap( editedIcon.toByteArray() ), mimeType );
    }

    @Activate
    public void start( final ComponentContext context )
    {
        context.getBundleContext().addBundleListener( this );

        for ( final Bundle bundle : context.getBundleContext().getBundles() )
        {
            if ( !isApplication( bundle ) )
            {
                continue;
            }

            addBundle( bundle );
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        final Bundle bundle = event.getBundle();

        // we cannot check if the bundle is an app when it is uninstalled
        if ( event.getType() == BundleEvent.UNINSTALLED )
        {
            removeBundle( bundle );
            return;
        }

        if ( !isApplication( bundle ) )
        {
            return;
        }

        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( bundle );
                break;
            default:
                break;
        }
    }

    /**
     * The persisted descriptor is written below the application node in system-repo: a change to it, here or on another node
     * of the cluster, invalidates the descriptor read for the application.
     */
    @Override
    public void onEvent( final Event event )
    {
        if ( !event.isType( "node.created" ) && !event.isType( "node.updated" ) && !event.isType( "node.deleted" ) )
        {
            return;
        }

        try
        {
            final Object nodes = event.getData().get( NODES_FIELD );
            if ( !( nodes instanceof List<?> entries ) )
            {
                return;
            }
            for ( final Object entry : entries )
            {
                if ( entry instanceof Map<?, ?> node )
                {
                    descriptorNodeApplicationKey( node ).ifPresent( this::unregisterApplicationDescriptor );
                }
            }
        }
        catch ( final Exception e )
        {
            LOG.warn( "Unable to handle event {}", event.getType(), e );
        }
    }

    private static Optional<ApplicationKey> descriptorNodeApplicationKey( final Map<?, ?> node )
    {
        if ( !SystemConstants.SYSTEM_REPO_ID.toString().equals( node.get( "repo" ) ) ||
            !SystemConstants.BRANCH_SYSTEM.getValue().equals( node.get( "branch" ) ) )
        {
            return Optional.empty();
        }
        final Object path = node.get( "path" );
        if ( !( path instanceof String pathString ) )
        {
            return Optional.empty();
        }
        final Matcher matcher = DESCRIPTOR_NODE_PATH.matcher( pathString );
        return matcher.matches() ? Optional.of( ApplicationKey.from( matcher.group( "name" ) ) ) : Optional.empty();
    }

    private boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getState() != Bundle.UNINSTALLED ) && ApplicationBundleUtils.isApplication( bundle );
    }

    private void addBundle( final Bundle bundle )
    {
        try
        {
            registerApplicationDescriptor( bundle );
        }
        catch ( final Exception t )
        {
            LOG.warn( "Unable to load application descriptor for {}", ApplicationBundleUtils.getApplicationName( bundle ), t );
        }
    }

    private void registerApplicationDescriptor( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationHelper.getApplicationKey( bundle );
        this.bundles.put( applicationKey, bundle );
        final ApplicationDescriptor applicationDescriptor =
            ApplicationDescriptorBuilder.build( applicationKey, factory.createUrlResolver( bundle, null ) );
        this.appDescriptorMap.put( applicationKey, applicationDescriptor );
    }

    private void removeBundle( final Bundle bundle )
    {
        final ApplicationKey applicationKey = ApplicationHelper.getApplicationKey( bundle );
        this.bundles.remove( applicationKey, bundle );
        unregisterApplicationDescriptor( applicationKey );
    }

    private void unregisterApplicationDescriptor( final ApplicationKey applicationKey )
    {
        this.appDescriptorMap.remove( applicationKey );
    }
}