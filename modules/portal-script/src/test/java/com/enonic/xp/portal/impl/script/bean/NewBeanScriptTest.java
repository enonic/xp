package com.enonic.xp.portal.impl.script.bean;

import org.junit.Test;

import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class NewBeanScriptTest
    extends AbstractScriptTest
{
    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "myapplication:/bean/simple-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }
}
