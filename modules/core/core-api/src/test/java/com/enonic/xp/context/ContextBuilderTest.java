package com.enonic.xp.context;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContextBuilderTest
{
    private static final class SampleValue
    {
    }

    @Test
    void testBuild()
    {
        final ContextBuilder builder = ContextBuilder.create();
        builder.repositoryId( "repository" );
        builder.branch( "branch" );

        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        builder.authInfo( authInfo );

        builder.attribute( "key1", "value1" );

        final SampleValue sampleValue = new SampleValue();
        builder.attribute( sampleValue );

        final Context context = builder.build();
        assertNotNull( context );
        assertEquals( "repository", context.getRepositoryId().toString() );
        assertEquals( "branch", context.getBranch().toString() );
        assertSame( authInfo, context.getAuthInfo() );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertSame( sampleValue, context.getAttribute( SampleValue.class ) );
    }

    @Test
    void testBuildFrom()
    {
        final Context old = ContextBuilder.create().
            repositoryId( "repository" ).
            branch( "branch" ).
            attribute( "key1", "value1" ).
            build();

        final ContextBuilder builder = ContextBuilder.from( old );

        final SampleValue sampleValue = new SampleValue();
        builder.attribute( sampleValue );

        final Context context = builder.build();
        assertNotNull( context );
        assertEquals( "repository", context.getRepositoryId().toString() );
        assertEquals( "branch", context.getBranch().toString() );
        assertEquals( AuthenticationInfo.unAuthenticated(), context.getAuthInfo() );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertSame( sampleValue, context.getAttribute( SampleValue.class ) );
    }

    @Test
    public void copyConstructWithOverwrite()
        throws Exception
    {

        final Context old = ContextBuilder.create().
            repositoryId( "repository" ).
            branch( "branch" ).
            attribute( "key1", "value1" ).
            build();

        final Branch newWS = Branch.from( "new" );
        final Context newContext = ContextBuilder.from( old ).
            branch( newWS ).
            build();

        assertEquals( newWS, newContext.getBranch() );
        assertEquals( "repository", newContext.getRepositoryId().toString() );
    }

    @Test
    void copyOf()
    {
        final Context oldCtx = ContextBuilder.create().attribute( "key1", "value1" ).build();

        oldCtx.getLocalScope().setAttribute( RepositoryId.from( "repository" ) );

        oldCtx.getLocalScope().setSession( new SessionMock() );

        oldCtx.getLocalScope().setAttribute( "duplicateKey", "localValue" );
        oldCtx.getLocalScope().getSession().setAttribute( "duplicateKey", "sessionValue" );

        oldCtx.getLocalScope().getSession().setAttribute( "sessionKey", "anotherSessionValue" );

        final Context newContext = ContextBuilder.copyOf( oldCtx ).build();

        assertNull( newContext.getLocalScope().getSession() );

        assertEquals( "repository", newContext.getRepositoryId().toString() );
        assertEquals( "localValue", newContext.getAttribute( "duplicateKey" ) );
        assertEquals( "anotherSessionValue", newContext.getAttribute( "sessionKey" ) );
    }
}
