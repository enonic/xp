package com.enonic.xp.script.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptRuntimeFactoryImplTest
{
    @Mock
    BundleContext bundleContext;

    @Mock(stubOnly = true)
    ResourceService resourceService;

    @BeforeEach
    void setup()
        throws Exception
    {
        // let the ServiceTracker construct and open against the mock registry (no initial services)
        lenient().when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );
    }

    @Test
    @SuppressWarnings("unchecked")
    void removedService_invalidatesRuntimes()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        scriptRuntimeFactory.removedService( mock( ServiceReference.class ), application );

        // app stop is a full instance teardown, not a name-keyed disposer lookup (#10844)
        verify( scriptRuntime ).invalidate( eq( applicationKey ) );
    }

    @Test
    void dispose_closesTheRuntime()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.dispose( scriptRuntime );

        verify( scriptRuntime ).close();
    }

    @Test
    void destroy_closesLeftoverRuntimes()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService ) );

        final ScriptRuntimeImpl disposed = mock( ScriptRuntimeImpl.class );
        final ScriptRuntimeImpl leftover = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( disposed, leftover );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        scriptRuntimeFactory.dispose( disposed );

        scriptRuntimeFactory.destroy();

        // the leftover runtime is swept exactly once; the disposed one is not closed again
        verify( disposed ).close();
        verify( leftover ).close();
        scriptRuntimeFactory.destroy();
        verify( disposed ).close();
        verify( leftover ).close();
    }
}
