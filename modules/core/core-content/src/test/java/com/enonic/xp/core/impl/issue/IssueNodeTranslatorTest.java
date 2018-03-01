package com.enonic.xp.core.impl.issue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.INDEX;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.core.impl.issue.IssuePropertyNames.TITLE;
import static org.junit.Assert.*;

public class IssueNodeTranslatorTest
{

    @Test
    public void testFromNodeNotResolvingChildren()
        throws Exception
    {
        final Node node = createNode();

        final Issue issue = IssueNodeTranslator.fromNode( node );
        final IssueName issueName = IssueName.from( NamePrettyfier.create( "title" ) );

        assertNotNull( issue );
        assertEquals( "title", issue.getTitle() );
        assertEquals( "description", issue.getDescription() );
        assertEquals( IssueStatus.OPEN, issue.getStatus() );
        assertEquals( PrincipalKey.from( "user:myStore:me" ), issue.getCreator() );
        assertEquals( PrincipalKey.from( "user:myStore:approver-1" ), issue.getApproverIds().first() );
        assertTrue( issue.getPublishRequest().getItems().contains(
            PublishRequestItem.create().id( ContentId.from( "content-id1" ) ).includeChildren( false ).build() ) );
        assertTrue( issue.getPublishRequest().getItems().contains(
            PublishRequestItem.create().id( ContentId.from( "content-id2" ) ).includeChildren( true ).build() ) );
        assertEquals( issueName, issue.getName() );
    }

    public static Node createNode()
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.addString( TITLE, "title" );
        issueAsData.addString( CREATOR, "user:myStore:me" );
        issueAsData.addString( STATUS, IssueStatus.OPEN.toString() );
        issueAsData.addString( DESCRIPTION, "description" );
        issueAsData.addLong( INDEX, 1L );

        issueAsData.addStrings( APPROVERS, "user:myStore:approver-1", "user:myStore:approver-2" );

        final PropertySet publishRequestSet = new PropertySet();

        List<PropertySet> propertySetList = Lists.newArrayList();
        propertySetList.add( createItemSet( "content-id1", false ) );
        propertySetList.add( createItemSet( "content-id2", true ) );
        publishRequestSet.addSets( PublishRequestPropertyNames.ITEMS, propertySetList.toArray( new PropertySet[propertySetList.size()] ) );
        issueAsData.addSet( PUBLISH_REQUEST, publishRequestSet );

        return Node.create().
            id( NodeId.from( UUID.randomUUID() ) ).
            name( IssueName.from( NamePrettyfier.create( "title" ) ).toString() ).
            parentPath( ContentConstants.CONTENT_ROOT_PATH ).
            data( propertyTree ).
            build();
    }

    private static PropertySet createItemSet( final String id, final boolean recursive )
    {
        final PropertySet itemSet = new PropertySet();
        itemSet.addString( PublishRequestPropertyNames.ITEM_ID, id );
        itemSet.addBoolean( PublishRequestPropertyNames.ITEM_RECURSIVE, recursive );
        return itemSet;
    }
}
