package com.enonic.xp.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentVersionTest
{
    @Test
    public void testBuilder()
    {
        final Instant now1 = Instant.now();
        final Instant now2 = Instant.now();

        final ContentVersionCommitInfo commitInfo = ContentVersionCommitInfo.create()
            .message( "My version 1" )
            .type( ContentVersionCommitInfo.CommitType.ARCHIVED )
            .publisher( PrincipalKey.ofAnonymous() )
            .timestamp( Instant.ofEpochSecond( 1562056003L ) )
            .build();

        assertEquals( ContentVersionCommitInfo.CommitType.ARCHIVED, commitInfo.getType() );
        assertEquals( "My version 1", commitInfo.getMessage() );
        assertEquals( PrincipalKey.ofAnonymous(), commitInfo.getPublisher() );
        assertEquals( Instant.ofEpochSecond( 1562056003L ), commitInfo.getTimestamp() );

        final WorkflowInfo workflowInfo = WorkflowInfo.create().state( WorkflowState.READY ).build();

        final AccessControlList permissions = AccessControlList.create()
            .add(
                AccessControlEntry.create().allow( Permission.CREATE, Permission.READ_PERMISSIONS ).principal( RoleKeys.EVERYONE ).build() )
            .build();

        final ContentPublishInfo publishInfo = ContentPublishInfo.create()
            .first( Instant.ofEpochSecond( 1562056004L ) )
            .from( Instant.ofEpochSecond( 1562056005L ) )
            .to( Instant.ofEpochSecond( 1562056006L ) )
            .build();

        final ContentVersion version = ContentVersion.create()
            .id( ContentVersionId.from( "a" ) )
            .path( ContentPath.from( ContentPath.ROOT, "a" ) )
            .modified( now1 )
            .timestamp( now2 )
            .childOrder( ChildOrder.manualOrder() )
            .modifier( PrincipalKey.ofAnonymous() )
            .displayName( "contentVersion" )
            .comment( "comment" )
            .publishInfo( publishInfo )
            .workflowInfo( workflowInfo )
            .permissions( permissions )
            .build();

        assertEquals( ContentVersionId.from( "a" ), version.getId() );
        assertEquals( now1, version.getModified() );
        assertEquals( now2, version.getTimestamp() );
        assertEquals( "comment", version.getComment() );
        assertEquals( PrincipalKey.ofAnonymous(), version.getModifier() );
        assertEquals( "contentVersion", version.getDisplayName() );
        assertEquals( publishInfo, version.getPublishInfo() );
        assertEquals( workflowInfo, version.getWorkflowInfo() );
        assertEquals( ChildOrder.manualOrder(), version.getChildOrder() );
        assertEquals( permissions, version.getPermissions() );
        assertEquals( "/a", version.getPath().toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentVersion.class ).verify();
    }
}
