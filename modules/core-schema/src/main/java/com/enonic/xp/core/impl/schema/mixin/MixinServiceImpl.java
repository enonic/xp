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

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService, BundleListener
{
    private final Map<ApplicationKey, Mixins> map;

    private ApplicationService applicationService;

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
        return getByModule( name.getApplicationKey() ).getMixin( name );
    }

    @Override
    public Mixin getByLocalName( final String localName )
    {
        return getAll().
            stream().
            filter( mixin -> mixin.getName().getLocalName().equals( localName ) ).
            findFirst().
            orElse( null );
    }

    @Override
    public Mixins getAll()
    {
        final Set<Mixin> mixinList = Sets.newLinkedHashSet();

        //Gets builtin mixins
        for ( ApplicationKey systemReservedApplicationKey : ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS )
        {
            final Mixins mixins = getByModule( systemReservedApplicationKey );
            mixinList.addAll( mixins.getList() );
        }

        //Gets modules mixins
        for ( Application application : this.applicationService.getAllModules() )
        {
            final Mixins mixins = getByModule( application.getKey() );
            mixinList.addAll( mixins.getList() );
        }

        return Mixins.from( mixinList );
    }

    @Override
    public Mixins getByModule( final ApplicationKey applicationKey )
    {
        return this.map.computeIfAbsent( applicationKey, this::loadByModule );
    }

    private Mixins loadByModule( final ApplicationKey applicationKey )
    {
        Mixins mixins = null;

        if ( ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS.contains( applicationKey ) )
        {
            mixins = new BuiltinMixinsLoader().loadByModule( applicationKey );
        }
        else
        {
            final Application application = this.applicationService.getModule( applicationKey );
            if ( application != null )
            {
                final MixinLoader mixinLoader = new MixinLoader( application.getBundle() );
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
            this.map.remove( ApplicationKey.from( event.getBundle() ) );
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
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

}
