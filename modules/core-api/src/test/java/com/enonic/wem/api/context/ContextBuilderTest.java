package com.enonic.wem.api.context;

import org.junit.Test;

import com.enonic.wem.api.security.auth.AuthenticationInfo;

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
        builder.workspace( "workspace" );

        final AuthenticationInfo authInfo = AuthenticationInfo.unAuthenticated();
        builder.authInfo( authInfo );

        builder.attribute( "key1", "value1" );

        final SampleValue sampleValue = new SampleValue();
        builder.attribute( sampleValue );

        final Context context = builder.build();
        assertNotNull( context );
        assertEquals( "repository", context.getRepositoryId().toString() );
        assertEquals( "workspace", context.getWorkspace().toString() );
        assertSame( authInfo, context.getAuthInfo() );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertSame( sampleValue, context.getAttribute( SampleValue.class ) );
    }

    @Test
    public void testBuildFrom()
    {
        final Context old = ContextBuilder.create().
            repositoryId( "repository" ).
            workspace( "workspace" ).
            attribute( "key1", "value1" ).
            build();

        final ContextBuilder builder = ContextBuilder.from( old );

        final SampleValue sampleValue = new SampleValue();
        builder.attribute( sampleValue );

        final Context context = builder.build();
        assertNotNull( context );
        assertEquals( "repository", context.getRepositoryId().toString() );
        assertEquals( "workspace", context.getWorkspace().toString() );
        assertEquals( AuthenticationInfo.unAuthenticated(), context.getAuthInfo() );
        assertEquals( "value1", context.getAttribute( "key1" ) );
        assertSame( sampleValue, context.getAttribute( SampleValue.class ) );
    }
}
