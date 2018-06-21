package com.enonic.xp.context;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;

import static org.junit.Assert.*;

public class ContextBuilderTest
{
    private final class SampleValue
    {
    }

    @Test
    public void testBuild()
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
    public void testBuildFrom()
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
    public void detachSession()
        throws Exception
    {
        final Context oldCtx = ContextBuilder.create().
            attribute( "key1", "value1" ).
            build();

        oldCtx.getLocalScope().setAttribute( Branch.from( "draft" ) );
        oldCtx.getLocalScope().setAttribute( RepositoryId.from( "repository" ) );
        oldCtx.getLocalScope().setSession( new SimpleSession( SessionKey.generate() ) );
        oldCtx.getLocalScope().getSession().setAttribute( "sessionKey", "sessionValue" );

        assertNotNull( oldCtx.getLocalScope().getSession() );
        assertEquals( "repository", oldCtx.getRepositoryId().toString() );
        assertEquals( "draft", oldCtx.getBranch().toString() );
        assertEquals( "sessionValue", oldCtx.getAttribute( "sessionKey" ) );

        final Context newContext = ContextBuilder.from( oldCtx ).
            detachSession().
            build();

        assertNull( newContext.getLocalScope().getSession() );
        assertEquals( "repository", newContext.getRepositoryId().toString() );
        assertEquals( "draft", newContext.getBranch().toString() );
        assertEquals( "sessionValue", newContext.getAttribute( "sessionKey" ) );
    }
}
