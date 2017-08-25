package com.enonic.xp.lib.mustache;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;

public class MustacheServiceTest
    extends ScriptTestSupport
{
    private MustacheService service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        this.service = new MustacheService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapp:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final MustacheProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/test/view/test.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
