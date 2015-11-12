package com.enonic.xp.lib.mustache;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

public class MustacheServiceTest
    extends ScriptBeanTestSupport
{
    private MustacheService service;

    @Override
    protected void initialize()
    {
        super.initialize();
        this.service = new MustacheService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapplication:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final MustacheProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapplication:/site/test/view/test-view.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
