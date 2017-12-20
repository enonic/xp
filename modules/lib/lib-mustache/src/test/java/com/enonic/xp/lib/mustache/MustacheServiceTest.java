package com.enonic.xp.lib.mustache;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceManager;

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

    @Test
    public void testWithTracingEnabled()
    {
        final TraceManager manager = Mockito.mock( TraceManager.class );
        final Trace trace = Mockito.mock( Trace.class );
        Mockito.when( manager.newTrace( Mockito.any(), Mockito.any() ) ).thenReturn( trace );
        com.enonic.xp.trace.Tracer.setManager( manager );

        final MustacheProcessor processor = this.service.newProcessor();
        processor.setView( ResourceKey.from( "myapp:/site/test/view/test.html" ) );
        processor.setModel( null );
        processor.process();
    }
}
