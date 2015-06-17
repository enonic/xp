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

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeRegistry;
import com.enonic.xp.schema.content.ContentTypes;

@Component(immediate = true)
public final class ContentTypeRegistryImpl
    implements ContentTypeRegistry, BundleListener
{
    private ModuleService moduleService;

    private BundleContext context;

    private final Map<ModuleKey, ContentTypes> map;

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
        return getByModule( name.getModuleKey() ).getContentType( name );
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        return this.map.computeIfAbsent( moduleKey, this::loadByModule );
    }

    private ContentTypes loadByModule( final ModuleKey moduleKey )
    {
        ContentTypes contentTypes = null;

        if ( ModuleKey.SYSTEM.equals( moduleKey ) )
        {
            contentTypes = new BuiltinContentTypeLoader().load();
        }
        else if ( ModuleKey.isSystemReservedModuleKey( moduleKey ) )
        {
            contentTypes = new BuiltinContentTypeLoader().loadByModule( moduleKey );
        }
        else
        {
            final Module module = this.moduleService.getModule( moduleKey );
            if ( module != null )
            {
                final ContentTypeLoader mixinLoader = new ContentTypeLoader( module.getBundle() );
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

        final ContentTypes systemContentTypes = getByModule( ModuleKey.SYSTEM );
        contentTypeList.addAll( systemContentTypes.getList() );

        for ( Module module : this.moduleService.getAllModules() )
        {
            final ContentTypes contentTypes = getByModule( module.getKey() );
            contentTypeList.addAll( contentTypes.getList() );
        }

        return ContentTypes.from( contentTypeList );
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( BundleEvent.UPDATED == event.getType() || BundleEvent.UNINSTALLED == event.getType() )
        {
            this.map.remove( ModuleKey.from( event.getBundle() ) );
        }
    }
}
