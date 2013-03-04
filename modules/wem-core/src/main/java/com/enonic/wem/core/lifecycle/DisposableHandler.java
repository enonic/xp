package com.enonic.wem.core.lifecycle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.spi.ProvisionListener;

final class DisposableHandler
    implements ProvisionListener
{
    private final static Logger LOG = LoggerFactory.getLogger( DisposableHandler.class );

    private final List<DisposableBean> beans;

    public DisposableHandler()
    {
        this.beans = Lists.newArrayList();
    }

    @Override
    public <T> void onProvision( final ProvisionInvocation<T> provision )
    {
        final Binding<?> binding = provision.getBinding();
        final Class<?> clz = binding.getKey().getTypeLiteral().getRawType();

        if ( DisposableBean.class.isAssignableFrom( clz ) )
        {
            this.beans.add( (DisposableBean) provision.provision() );
        }
    }

    public void disposeAll()
    {
        for ( final DisposableBean bean : this.beans )
        {
            dispose( bean );
        }
    }

    private void dispose( final DisposableBean bean )
    {
        try
        {
            bean.destroy();
        }
        catch ( final Exception e )
        {
            LOG.warn( "Error occurred on " + bean.getClass().getName() + ".dispose()", e );
        }
    }
}
