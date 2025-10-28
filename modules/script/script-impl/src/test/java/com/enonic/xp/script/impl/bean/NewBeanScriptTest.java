package com.enonic.xp.script.impl.bean;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.impl.AbstractScriptTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class NewBeanScriptTest
    extends AbstractScriptTest
{
    @Test
    void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/bean/simple-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }
}
