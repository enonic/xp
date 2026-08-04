package com.enonic.xp.script.runtime;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BootstrapParamsTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "myapp" );

    @Test
    void mainScriptIsOptional()
    {
        final BootstrapParams params = BootstrapParams.create().application( APP ).build();

        assertEquals( APP, params.getApplication() );
        assertEquals( Optional.empty(), params.getMainScript() );
    }

    @Test
    void mainScriptMustBelongToTheApplication()
    {
        final ResourceKey foreignScript = ResourceKey.from( ApplicationKey.from( "otherapp" ), "/main.js" );
        final BootstrapParams.Builder builder = BootstrapParams.create().application( APP ).mainScript( foreignScript );

        assertThrows( IllegalArgumentException.class, builder::build );
    }

    @Test
    void applicationIsRequired()
    {
        final BootstrapParams.Builder builder = BootstrapParams.create();

        assertThrows( NullPointerException.class, builder::build );
    }
}
