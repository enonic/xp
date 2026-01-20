package com.enonic.xp.script.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptRuntimeFactoryImplTest
{
    @Mock(stubOnly = true)
    BundleContext bundleContext;

    @Mock(stubOnly = true)
    ResourceService resourceService;

    @Mock(stubOnly = true)
    ScriptAsyncService scriptAsyncService;

    @Test
    void invalidate()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService, scriptAsyncService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );
        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );
        scriptRuntimeFactory.invalidate( applicationKey, ApplicationInvalidationLevel.FULL );

        verify( scriptRuntime ).invalidate( eq( applicationKey ) );
    }

    @Test
    void deactivate()
    {
        final ScriptRuntimeFactoryImpl scriptRuntimeFactory =
            spy( new ScriptRuntimeFactoryImpl( bundleContext, resourceService, scriptAsyncService ) );

        final ScriptRuntimeImpl scriptRuntime = mock( ScriptRuntimeImpl.class );
        when( scriptRuntimeFactory.doCreate( any() ) ).thenReturn( scriptRuntime );

        scriptRuntimeFactory.create( ScriptSettings.create().build() );

        final ApplicationKey applicationKey = ApplicationKey.from( "myapp" );

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( applicationKey );
        scriptRuntimeFactory.deactivated( application );

        verify( scriptRuntime ).runDisposers( eq( applicationKey ) );
    }
}
