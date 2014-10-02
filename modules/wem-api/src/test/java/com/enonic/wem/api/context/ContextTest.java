package com.enonic.wem.api.context;

import org.junit.Test;

import junit.framework.Assert;

import com.enonic.wem.api.workspace.Workspace;

public class ContextTest
{
    @Test
    public void testSomething()
    {
        final Context2 current = Context2.current();
        Assert.assertNotNull( current );

        final Context2 context = ContextBuilder.create().workspace( "test" ).build();

        System.out.println( context.runWith( this::callWithOtherContext ) );

    }

    private String callWithOtherContext()
    {
        final Context2 current = Context2.current();
        Assert.assertNotNull( current );
        Assert.assertEquals( Workspace.from( "test" ), current.getWorkspace() );
        return "Hello " + current.getWorkspace();
    }
}
