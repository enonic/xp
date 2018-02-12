package com.enonic.xp.core.impl.issue.serializer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATED_TIME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR_DISPLAY_NAME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.TEXT;
import static org.junit.Assert.*;

public class IssueCommentDataSerializerTest
{

    private IssueCommentDataSerializer serializer;

    private PrincipalKey creator;

    private Instant createdTime;

    @Before
    public void setUp()
    {
        serializer = new IssueCommentDataSerializer();
        creator = PrincipalKey.from( "user:store:one" );
        createdTime = Instant.now().minus( 1, ChronoUnit.MINUTES );
    }

    @Test
    public void testCreate()
    {
        final CreateIssueCommentParams params = CreateIssueCommentParams.create().
            issue( IssueId.create() ).
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

    @Test
    public void testUpdate()
    {
        PropertyTree data = new PropertyTree();
        data.addString( TEXT, "Comment text..." );

        serializer.updateNodeData( data, UpdateIssueCommentParams.create().text( "Updated text" ).build() );

        assertEquals( data.getString( TEXT ), "Updated text" );
    }

    @Test
    public void testDeserialize()
    {
        PropertyTree data = new PropertyTree();
        data.addString( TEXT, "Comment text..." );
        data.addString( CREATOR_DISPLAY_NAME, "Creator One" );
        data.addString( CREATOR, creator.toString() );
        data.addInstant( CREATED_TIME, createdTime );

        IssueComment comment = serializer.fromData( data ).build();

        assertNotNull( comment );
        assertEquals( "Comment text...", comment.getText() );
        assertEquals( "Creator One", comment.getCreatorDisplayName() );
        assertEquals( creator, comment.getCreator() );
        assertEquals( createdTime, comment.getCreated() );
    }
}
