package com.enonic.xp.portal.impl.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PortalScriptServiceImplTest
{
    @Mock
    ScriptRuntime scriptRuntime;

    PortalScriptServiceImpl portalScriptService;

    final ResourceKey resourceKey = ResourceKey.from( ApplicationKey.from( "myapp" ), "main.js" );

    private ScriptRuntimeFactory scriptRuntimeFactory()
    {
        return new ScriptRuntimeFactory()
        {
            @Override
            public ScriptRuntime create( final ScriptSettings settings )
            {
                return scriptRuntime;
            }

            @Override
            public void dispose( final ScriptRuntime runtime )
            {

            }
        };
    }

    @BeforeEach
    void setUp()
    {
        portalScriptService = new PortalScriptServiceImpl( scriptRuntimeFactory() );
        portalScriptService.initialize();
    }

    @Test
    void execute_awaitsBootstrapForController()
    {
        final List<ApplicationKey> awaited = new ArrayList<>();
        final PortalScriptServiceImpl service = new PortalScriptServiceImpl( scriptRuntimeFactory(), awaited::add );
        service.initialize();

        service.execute( ResourceKey.from( ApplicationKey.from( "myapp" ), "/controllers/foo.js" ) );

        assertEquals( List.of( ApplicationKey.from( "myapp" ) ), awaited );
    }

    @Test
    void execute_doesNotAwaitForMainJs()
    {
        final List<ApplicationKey> awaited = new ArrayList<>();
        final PortalScriptServiceImpl service = new PortalScriptServiceImpl( scriptRuntimeFactory(), awaited::add );
        service.initialize();

        service.execute( ResourceKey.from( ApplicationKey.from( "myapp" ), "/main.js" ) );

        assertTrue( awaited.isEmpty() );
    }

    @Test
    void hasScript()
    {

        portalScriptService.hasScript( resourceKey );
        verify( scriptRuntime ).hasScript( eq( resourceKey ) );
    }

    @Test
    void execute()
    {
        portalScriptService.execute( ResourceKey.from( ApplicationKey.from( "myapp" ), "main.js" ) );
        verify( scriptRuntime ).execute( eq( resourceKey ) );
    }

    @Test
    void executeAsync()
    {
        portalScriptService.executeAsync( ResourceKey.from( ApplicationKey.from( "myapp" ), "main.js" ) );
        verify( scriptRuntime ).executeAsync( eq( resourceKey ) );
    }

    @Test
    void toScriptValue()
    {
        portalScriptService.toScriptValue( ResourceKey.from( ApplicationKey.from( "myapp" ), "main.js" ), Collections.emptyList() );
        verify( scriptRuntime ).toScriptValue( eq( resourceKey ), eq( Collections.emptyList() ) );
    }

    @Test
    void toNativeObject()
    {
        portalScriptService.toNativeObject( ResourceKey.from( ApplicationKey.from( "myapp" ), "main.js" ), Collections.emptyMap() );
        verify( scriptRuntime ).toNativeObject( eq( resourceKey ), eq( Collections.emptyMap() ) );
    }
}