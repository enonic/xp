package com.enonic.wem.core.lifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Binding;
import com.google.inject.Injector;

@Singleton
public final class LifecycleServiceImpl
    implements LifecycleService
{
    private final Multimap<RunLevel, LifecycleBean> map;

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
        catch ( final Exception e )
        {
            stopAll();
        }
    }

    private void doStartAll()
        throws Exception
    {
        for ( final RunLevel level : RunLevel.all() )
        {
            doStart( level );
        }
    }

    @Override
    public void stopAll()
    {
        for ( final RunLevel level : RunLevel.reverse() )
        {
            doStop( level );
        }
    }

    private void doStart( final RunLevel level )
        throws Exception
    {
        for ( final LifecycleBean bean : this.map.get( level ) )
        {
            bean.start();
        }
    }

    private void doStop( final RunLevel level )
    {
        for ( final LifecycleBean bean : this.map.get( level ) )
        {
            bean.stop();
        }
    }

    private void addLifecycleBeans( final Injector injector )
    {
        for ( final Binding<?> binding : injector.getAllBindings().values() )
        {
            addLifecycleBean( binding );
        }
    }

    private void addLifecycleBean( final Binding binding )
    {
        if ( !isLifecycleBean( binding ) )
        {
            return;
        }

        final LifecycleBean bean = (LifecycleBean) binding.getProvider().get();
        this.map.put( bean.getRunLevel(), bean );
    }

    private boolean isLifecycleBean( final Binding<?> binding )
    {
        final Class<?> type = binding.getKey().getTypeLiteral().getRawType();
        return LifecycleBean.class.isAssignableFrom( type );
    }
}
