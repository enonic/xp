package com.enonic.xp.core.impl.issue.serializer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATED_TIME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR_DISPLAY_NAME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.TEXT;
import static org.junit.Assert.*;

public class IssueCommentDataSerializerTest
{
    @Test
    public void testCreate()
    {
        IssueCommentDataSerializer serializer = new IssueCommentDataSerializer();

        final PrincipalKey creator = PrincipalKey.from( "user:store:one" );
        final IssueName issue = IssueName.from( "issue-1" );
        final Instant createdTime = Instant.now().minus( 1, ChronoUnit.MINUTES );
        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            issueName( issue ).
            creator( creator ).
            creatorDisplayName( "Creator One" ).
            text( "Comment text..." ).
            created( createdTime ).
            build();

        final PropertyTree data = serializer.toCreateNodeData( params );

        assertNotNull( data );
        assertEquals( "Comment text...", data.getString( TEXT ) );
        assertEquals( "Creator One", data.getString( CREATOR_DISPLAY_NAME ) );
        assertEquals( creator.toString(), data.getString( CREATOR ) );
        assertEquals( createdTime, data.getInstant( CREATED_TIME ) );
    }
}
