package com.enonic.xp.core.impl.issue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATED_TIME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR_DISPLAY_NAME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.TEXT;
import static org.junit.Assert.*;

public class IssueCommentNodeTranslatorTest
{

    @Test
    public void testFromNodeNotResolvingChildren()
        throws Exception
    {
        final Instant created = Instant.now().minus( 1, ChronoUnit.MINUTES );
        final Node node = createNode( created );
        final IssueComment comment = IssueCommentNodeTranslator.fromNode( node );

        assertNotNull( comment );
        assertEquals( node.id(), comment.getId() );
        assertEquals( "title", comment.getText() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), comment.getCreator() );
        assertEquals( "Me Myself", comment.getCreatorDisplayName() );
        assertEquals( created, comment.getCreated() );
    }

    public static Node createNode( Instant created )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.addString( TEXT, "title" );
        issueAsData.addString( CREATOR, "user:myStore:me" );
        issueAsData.addString( CREATOR_DISPLAY_NAME, "Me Myself" );
        issueAsData.addInstant( CREATED_TIME, created );

        return Node.create().
            id( NodeId.from( UUID.randomUUID() ) ).
            name( NodeName.from( "name" ) ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( propertyTree ).
            build();
    }
}
