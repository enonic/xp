package com.enonic.xp.portal.impl.script.bean;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class NewBeanScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
        throws Exception
    {
        mockResource( "mymodule:/bean/simple-test.js" );
    }

    @Test
    public void testEmpty()
    {
        final ResourceKey script = ResourceKey.from( "mymodule:/bean/simple-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }
}
