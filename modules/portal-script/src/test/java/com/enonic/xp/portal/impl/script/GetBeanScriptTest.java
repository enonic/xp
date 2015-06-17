package com.enonic.xp.portal.impl.script;

import org.junit.Test;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.Assert.*;

public class GetBeanScriptTest
    extends AbstractScriptTest
{
    public class MyBean
    {
        private int count = 0;

        public int getCount()
        {
            return this.count++;
        }

        public void setCount( final int value )
        {
            this.count = value;
        }
    }

    @Test
    public void testEmpty()
    {
        this.beanManager.register( ModuleKey.from( "mymodule" ), "mybean", new MyBean() );
        final ResourceKey script = ResourceKey.from( "mymodule:/bean/simple-test.js" );
        final ScriptExports exports = runTestScript( script );
        assertNotNull( exports );
        assertSame( script, exports.getScript() );
    }
}
