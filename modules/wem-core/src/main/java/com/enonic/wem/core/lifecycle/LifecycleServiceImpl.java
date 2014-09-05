package com.enonic.wem.core.lifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Binding;
import com.google.inject.Injector;

@Singleton
final class LifecycleServiceImpl
    implements LifecycleService
{
    private final Multimap<LifecycleStage, LifecycleBean> map;

    @Inject
    public LifecycleServiceImpl( final Injector injector )
    {
        this.map = HashMultimap.create();
        addLifecycleBeans( injector );
    }

    @Override
    public void startAll()
    {
        try
        {
            doStartAll();
        }
        catch ( final RuntimeException e )
        {
            stopAll();
            throw e;
        }
    }

    private void doStartAll()
    {
        LifecycleStage.all().forEach( this::doStart );
    }

    @Override
    public void stopAll()
    {
        LifecycleStage.reverse().forEach( this::doStop );
    }

    private void doStart( final LifecycleStage level )
    {
        for ( final LifecycleBean bean : this.map.get( level ) )
        {
            bean.start();
        }
    }

    private void doStop( final LifecycleStage level )
    {
        for ( final LifecycleBean bean : this.map.get( level ) )
        {
            bean.stop();
        }
    }

    private void addLifecycleBeans( final Injector injector )
    {
        injector.getAllBindings().values().forEach( this::addLifecycleBean );
    }

    private void addLifecycleBean( final Binding binding )
    {
        if ( !isLifecycleBean( binding ) )
        {
            return;
        }

        final LifecycleBean bean = (LifecycleBean) binding.getProvider().get();
        this.map.put( bean.getStage(), bean );
    }

    private boolean isLifecycleBean( final Binding<?> binding )
    {
        final Class<?> type = binding.getKey().getTypeLiteral().getRawType();
        return LifecycleBean.class.isAssignableFrom( type );
    }
}
