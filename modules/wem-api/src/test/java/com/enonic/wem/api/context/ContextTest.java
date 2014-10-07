package com.enonic.wem.api.context;

import org.junit.Test;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

import static org.junit.Assert.*;

public class ContextTest
{

    @Test
    public void switch_context()
        throws Exception
    {
        final Context outerContext = ContextBuilder.create().
            object( Workspace.from( "ws-1" ) ).
            object( RepositoryId.from( "repo1" ) ).
            build();

        outerContext.runWith( () -> {

            assertEquals( outerContext, Context.current() );

            final Context innerContext = ContextBuilder.create().
                object( Workspace.from( "ws-2" ) ).
                object( RepositoryId.from( "repo2" ) ).
                build();

            innerContext.runWith( () -> assertEquals( innerContext, Context.current() ) );

            assertEquals( outerContext, Context.current() );
        } );
    }
}
