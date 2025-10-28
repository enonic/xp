package com.enonic.xp.core.impl.issue.serializer;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.core.impl.issue.PublishRequestPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.security.PrincipalKeys;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IssueDataSerializerTest
{
    @Test
    void testCreate()
    {
        IssueDataSerializer issueDataSerializer = new IssueDataSerializer();
        CreateIssueParams params = CreateIssueParams.create().
            title( "title" ).
            description( "descr" ).
            setApproverIds( PrincipalKeys.from( "user:myStore:approver" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( false ).build() ).addExcludeId(
                ContentId.from( "exclude-id" ) ).build() ).
            build();

        final PropertyTree data = issueDataSerializer.toCreateNodeData( params );

        assertNotNull( data );
        assertEquals( "title", data.getString( TITLE ) );
        assertEquals( "descr", data.getString( DESCRIPTION ) );
        assertEquals( "OPEN", data.getString( STATUS ) );
        assertEquals( "user:myStore:approver", data.getStrings( APPROVERS ).iterator().next() );
        assertEquals( "exclude-id",
                      data.getSet( PUBLISH_REQUEST ).getStrings( PublishRequestPropertyNames.EXCLUDE_IDS ).iterator().next() );
        final Iterable<PropertySet> itemSets = data.getSet( PUBLISH_REQUEST ).getSets( PublishRequestPropertyNames.ITEMS );

        final ArrayList<PropertySet> itemSetsAsList = Lists.newArrayList( itemSets );
        assertEquals( 1, itemSetsAsList.size() );
        assertFalse( itemSetsAsList.get( 0 ).getBoolean( PublishRequestPropertyNames.ITEM_RECURSIVE ) );

    }
}
