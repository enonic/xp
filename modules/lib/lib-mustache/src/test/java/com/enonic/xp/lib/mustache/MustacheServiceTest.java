package com.enonic.xp.lib.mustache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptBeanTestSupport;

public class MustacheServiceTest
    extends ScriptBeanTestSupport
{
    private MustacheService service;

    @Before
    public void setup()
    {
        setupRequest();
        addService( ViewFunctionService.class, Mockito.mock( ViewFunctionService.class, (Answer) this::urlAnswer ) );

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

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        final ViewFunctionParams params = (ViewFunctionParams) invocation.getArguments()[0];
        return params.getName() + "(" + params.getArgs().toString() + ")";
    }
}
