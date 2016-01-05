package com.enonic.xp.lib.xslt;

import org.junit.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

public class XsltServiceTest
    extends ScriptBeanTestSupport
{
    private XsltService service;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        this.service = new XsltService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapp:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final XsltProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/view/simple.xsl" ) );
        processor.setModel( null );
        processor.process();
    }
}
