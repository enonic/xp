package com.enonic.xp.lib.xslt;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

public class XsltServiceTest
    extends ScriptBeanTestSupport
{
    private XsltService service;

    @Override
    protected void initialize()
    {
        super.initialize();
        addService( ViewFunctionService.class, Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );

        this.service = new XsltService();
        this.service.initialize( newBeanContext( ResourceKey.from( "myapplication:/site" ) ) );
    }

    @Test
    public void testProcess()
    {
        final XsltProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapplication:/site/view/simple.xsl" ) );
        processor.setModel( null );
        processor.process();
    }

    private Object urlAnswer( final InvocationOnMock invocation )
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}
