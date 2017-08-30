package com.enonic.xp.lib.thymeleaf;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.testing.ScriptTestSupport;

public class ThymeleafServiceTest
    extends ScriptTestSupport
{
    private ThymeleafService service;

    @Override
    protected void initialize()
        throws Exception
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
        processor.setMode( null );
        processor.process();
    }

    @Test(expected = ResourceProblemException.class)
    public void testProcessError()
    {
        final ThymeleafProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/view/error.html" ) );
        processor.setModel( null );
        processor.setMode( null );
        processor.process();
    }
}
