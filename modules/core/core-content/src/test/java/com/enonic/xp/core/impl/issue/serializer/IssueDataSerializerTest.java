package com.enonic.xp.core.impl.issue.serializer;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.issue.PublishRequestPropertyNames;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static org.junit.Assert.*;

public class IssueDataSerializerTest
{
    @Test
    public void testCreate()
    {
        IssueDataSerializer issueDataSerializer = new IssueDataSerializer();
        CreateIssueParams params = CreateIssueParams.create().
            title( "title" ).
            description( "descr" ).
            addApproverId( PrincipalKey.from( "user:myStore:approver" ) ).
            setPublishRequest( PublishRequest.create().addItem(
                PublishRequestItem.create().id( ContentId.from( "content-id" ) ).includeChildren( false ).build() ).addExcludeId(
                ContentId.from( "exclude-id" ) ).build() ).
            build();

        final PropertyTree data = issueDataSerializer.toCreateNodeData( params );

        assertNotNull( data );
        assertEquals( "title", data.getString( TITLE ) );
        assertEquals( "descr", data.getString( DESCRIPTION ) );
        assertEquals( "Open", data.getString( STATUS ) );
        assertEquals( "user:myStore:approver", data.getStrings( APPROVERS ).iterator().next() );
        assertEquals( "exclude-id", data.getSet( PUBLISH_REQUEST ).getStrings( PublishRequestPropertyNames.EXCLUDE_IDS ).iterator().next() );
        assertEquals( false, data.getSet( PUBLISH_REQUEST ).getSet( PublishRequestPropertyNames.ITEMS ).getBoolean( "content-id" ) );
    }
}
