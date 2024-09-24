package com.enonic.xp.lib.admin;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class LibAdminTest
    extends ScriptTestSupport
{
    @Test
    void testWidgetUrl()
    {
        runFunction( "/test/admin-test.js", "testWidgetUrl" );
    }
}
