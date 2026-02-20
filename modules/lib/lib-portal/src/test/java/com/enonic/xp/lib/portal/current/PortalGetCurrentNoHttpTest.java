package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortalGetCurrentNoHttpTest
    extends ScriptTestSupport
{
    PortalGetCurrentNoHttpTest()
    {
        super( false );
    }

    @Test
    void getComponent()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getComponent.js" ) ).hasMessage( "no request bound" );
    }

    @Test
    void getContent()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getContent.js" ) ).hasMessage( "no request bound" );
    }

    @Test
    void getSite()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getSite.js" ) ).hasMessage( "no request bound" );
    }

    @Test
    void getSiteConfig()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getSiteConfig.js" ) ).hasMessage( "no request bound" );
    }

    @Test
    void getIdProviderKey()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getIdProviderKey.js" ) ).hasMessage( "no request bound" );
    }

    @Test
    void getMultipartForm()
    {
        assertThatThrownBy( () -> runScript( "/lib/xp/examples/portal/getMultipartForm.js" ) ).hasMessage( "no request bound" );
    }
}
