package com.enonic.xp.core.impl.schema.mixin;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService, BundleListener
{
    private final Map<ModuleKey, Mixins> map;

    private ModuleService moduleService;

    private BundleContext context;

    public MixinServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

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

    @Override
    public Mixin getByName( final MixinName name )
    {
        return getByModule( name.getModuleKey() ).getMixin( name );
    }

    @Override
    public Mixin getByLocalName( final String localName )
    {
        return this.map.values().stream().
            flatMap( mixins -> mixins.stream() ).
            filter( ( mixin ) -> mixin.getName().getLocalName().equals( localName ) ).
            findFirst().orElse( null );
    }

    @Override
    public Mixins getAll()
    {
        final Set<Mixin> mixinList = Sets.newLinkedHashSet();

        final Mixins systemMixins = getByModule( ModuleKey.SYSTEM );
        mixinList.addAll( systemMixins.getList() );

        for ( Module module : this.moduleService.getAllModules() )
        {
            final Mixins mixins = getByModule( module.getKey() );
            mixinList.addAll( mixins.getList() );
        }

        return Mixins.from( mixinList );
    }

    @Override
    public Mixins getByModule( final ModuleKey moduleKey )
    {
        return this.map.computeIfAbsent( moduleKey, this::loadByModule );
    }

    private Mixins loadByModule( final ModuleKey moduleKey )
    {
        Mixins mixins = null;

        if ( ModuleKey.SYSTEM.equals( moduleKey ) )
        {
            mixins = new BuiltinMixinsLoader().load();
        }
        else if ( ModuleKey.isSystemReservedModuleKey( moduleKey ) )
        {
            mixins = new BuiltinMixinsLoader().loadByModule( moduleKey );
        }
        else
        {
            final Module module = this.moduleService.getModule( moduleKey );
            if ( module != null )
            {
                final MixinLoader mixinLoader = new MixinLoader( module.getBundle() );
                mixins = mixinLoader.loadMixins();
            }
        }

        if ( mixins == null )
        {
            mixins = Mixins.empty();
        }

        return mixins;
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( BundleEvent.UPDATED == event.getType() || BundleEvent.UNINSTALLED == event.getType() )
        {
            this.map.remove( ModuleKey.from( event.getBundle() ) );
        }
    }

    @Override
    public Mixins getByContentType( final ContentType contentType )
    {
        return Mixins.from( contentType.getMetadata().stream().
            map( this::getByName ).
            filter( Objects::nonNull ).
            collect( Collectors.toSet() ) );
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

}
