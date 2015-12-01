package com.enonic.xp.lib.thymeleaf;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

public class ThymeleafServiceTest
    extends ScriptBeanTestSupport
{
    private ThymeleafService service;

    @Override
    protected void initialize()
    {
        super.initialize();
        this.service = new ThymeleafService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapplication:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final ThymeleafProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapplication:/site/view/test.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
