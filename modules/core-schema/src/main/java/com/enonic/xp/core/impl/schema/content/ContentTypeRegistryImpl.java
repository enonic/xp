package com.enonic.xp.core.impl.schema.content;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeRegistry;
import com.enonic.xp.schema.content.ContentTypes;

@Component(immediate = true)
public final class ContentTypeRegistryImpl
    implements ContentTypeRegistry, BundleListener
{
    private ApplicationService applicationService;

    private BundleContext context;

    private final Map<ApplicationKey, ContentTypes> map;

    @Activate
    public void start( final ComponentContext context )
    {
        this.context = context.getBundleContext();
        this.context.addBundleListener( this );
    }

    @Deactivate
    public void stop()
    {
        this.context.removeBundleListener( this );
    }

    public ContentTypeRegistryImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public ContentType get( final ContentTypeName name )
    {
        return getByApplication( name.getApplicationKey() ).getContentType( name );
    }

    @Override
    public ContentTypes getByApplication( final ApplicationKey applicationKey )
    {
        return this.map.computeIfAbsent( applicationKey, this::loadByApplication );
    }

    private ContentTypes loadByApplication( final ApplicationKey applicationKey )
    {
        ContentTypes contentTypes = null;

        if ( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( applicationKey ) )
        {
            contentTypes = new BuiltinContentTypeLoader().loadByApplication( applicationKey );
        }
        else
        {
            final Application application = this.applicationService.getApplication( applicationKey );
            if ( application != null )
            {
                final ContentTypeLoader mixinLoader = new ContentTypeLoader( application.getBundle() );
                contentTypes = mixinLoader.load();
            }
        }

        if ( contentTypes == null )
        {
            contentTypes = ContentTypes.empty();
        }

        return contentTypes;
    }

    @Override
    public ContentTypes getAll()
    {
        final Set<ContentType> contentTypeList = Sets.newLinkedHashSet();

        //Gets builtin content types
        for ( ApplicationKey systemReservedApplicationKey : ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS )
        {
            final ContentTypes contentTypes = getByApplication( systemReservedApplicationKey );
            contentTypeList.addAll( contentTypes.getList() );
        }

        //Gets application content types
        for ( Application application : this.applicationService.getAllApplications() )
        {
            final ContentTypes contentTypes = getByApplication( application.getKey() );
            contentTypeList.addAll( contentTypes.getList() );
        }

        return ContentTypes.from( contentTypeList );
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( BundleEvent.UPDATED == event.getType() || BundleEvent.UNINSTALLED == event.getType() )
        {
            this.map.remove( ApplicationKey.from( event.getBundle() ) );
        }
    }
}
