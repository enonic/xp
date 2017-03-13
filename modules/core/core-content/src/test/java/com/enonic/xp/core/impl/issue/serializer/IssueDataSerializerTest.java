package com.enonic.xp.core.impl.issue.serializer;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.ITEMS;
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
            creator( PrincipalKey.from( "user:myStore:me" ) ).
            addApproverId( PrincipalKey.from( "user:myStore:approver" ) ).
            addItemId( ContentId.from( "content-id" ) ).
            build();

        final PropertyTree data = issueDataSerializer.toCreateNodeData( params );

        assertNotNull( data );
        assertEquals( "title", data.getString( TITLE ) );
        assertEquals( "descr", data.getString( DESCRIPTION ) );
        assertEquals( "Open", data.getString( STATUS ) );
        assertEquals( "user:myStore:me", data.getString( CREATOR ) );
        assertEquals( "user:myStore:approver", data.getStrings( APPROVERS ).iterator().next() );
        assertEquals( "content-id", data.getStrings( ITEMS ).iterator().next() );
    }
}
