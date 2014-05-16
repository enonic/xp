package com.enonic.wem.guice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ops4j.peaberry.Export;
import org.ops4j.peaberry.Import;
import org.ops4j.peaberry.Peaberry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import com.enonic.wem.guice.binder.OsgiModule;

public abstract class GuiceActivator
    extends OsgiModule
    implements BundleActivator
{
    private BundleContext context;

    private Injector injector;

    private List<Export<?>> exports;

    protected final BundleContext getContext()
    {
        return this.context;
    }

    @Override
    public final void start( final BundleContext context )
        throws Exception
    {
        this.context = context;
        startModule();
        doStart();
    }

    @Override
    public final void stop( final BundleContext context )
        throws Exception
    {
        try
        {
            stopModule();
            doStop();
        }
        finally
        {
            this.injector = null;
        }
    }

    private void startModule()
    {
        this.injector = Guice.createInjector( createModules( this.context ) );
        this.injector.injectMembers( this );
        createExports();
    }

    private void createExports()
    {
        this.exports = new ArrayList<>();
        for ( final Map.Entry<Key<?>, Binding<?>> e : this.injector.getBindings().entrySet() )
        {
            final Key<?> k = e.getKey();
            if ( Export.class == k.getTypeLiteral().getRawType() )
            {
                this.exports.add( (Export<?>) this.injector.getInstance( k ) );
            }
        }
    }

    private Iterable<Module> createModules( final BundleContext context )
    {
        final ArrayList<Module> modules = new ArrayList<>();
        modules.add( Peaberry.osgiModule( context ) );
        modules.add( this );
        return modules;
    }

    private void stopModule()
    {
        this.exports.forEach( Import::unget );
    }

    protected void doStart()
        throws Exception
    {
        // Do nothing. Override to implement custom logic.
    }

    protected void doStop()
        throws Exception
    {
        // Do nothing. Override to implement custom logic.
    }
}
