package com.enonic.xp.core.impl.issue;

import java.util.UUID;

import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssuePath;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequestPropertyNames;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static org.junit.Assert.*;

public class IssueNodeTranslatorTest
{

    private static final IssueNodeTranslator ISSUE_NODE_TRANSLATOR = new IssueNodeTranslator();

    @Test
    public void testFromNodeNotResolvingChildren()
        throws Exception
    {
        final Node node = createNode();

        final Issue issue = this.ISSUE_NODE_TRANSLATOR.fromNode( node );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.Open, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertEquals( ContentId.from( "content-id1" ), issue.getPublishRequest().getItems().first().getId() );
        assertEquals( false, issue.getPublishRequest().getItems().first().getIncludeChildren() );
        assertEquals( issueName, issue.getName() );
        assertEquals( IssuePath.from( issueName ), issue.getPath() );
    }

    private Node createNode()
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.addString( TITLE, "title" );
        issueAsData.addString( CREATOR, "user:myStore:me" );
        issueAsData.addString( STATUS, IssueStatus.Open.toString() );
        issueAsData.addString( DESCRIPTION, "description" );

        issueAsData.addStrings( APPROVERS, "user:myStore:approver-1", "user:myStore:approver-2" );

        final PropertySet publishRequestSet = new PropertySet();
        final PropertySet itemsSet = new PropertySet();
        itemsSet.addBoolean( "content-id1", false );
        itemsSet.addBoolean( "content-id2", true );

        publishRequestSet.addSet( PublishRequestPropertyNames.ITEMS, itemsSet );
        issueAsData.addSet( PUBLISH_REQUEST, publishRequestSet );

        return Node.create().
            id( NodeId.from( UUID.randomUUID() ) ).
            name( IssueName.from( NamePrettyfier.create( "title" ) ).toString() ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( propertyTree ).
            build();
    }
}
