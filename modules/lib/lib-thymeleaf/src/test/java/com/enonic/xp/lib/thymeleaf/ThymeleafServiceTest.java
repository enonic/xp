package com.enonic.xp.lib.thymeleaf;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport2;

public class ThymeleafServiceTest
    extends ScriptBeanTestSupport2
{
    private ThymeleafService service;

    @Override
    protected void initialize()
    {
        super.initialize();
        this.service = new ThymeleafService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapp:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final ThymeleafProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/view/test.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
