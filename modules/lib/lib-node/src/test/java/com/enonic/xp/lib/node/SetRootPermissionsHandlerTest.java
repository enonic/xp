package com.enonic.xp.lib.node;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

public class SetRootPermissionsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void example()
    {
        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( RoleKeys.ADMIN ).
                allowAll().
                build() ).
            build();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "cms-repo" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "cms-repo" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        Mockito.when( this.nodeService.setRootPermissions( acl, true ) ).
            thenReturn( Node.create().
                permissions( acl ).
                inheritPermissions( true ).
                build() );

        runScript( "/site/lib/xp/examples/node/modifyRootPermissions.js" );
    }

}