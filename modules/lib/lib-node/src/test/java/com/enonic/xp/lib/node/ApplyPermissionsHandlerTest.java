package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

class ApplyPermissionsHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    void example()
    {
        final AccessControlList acl =
            AccessControlList.create().add( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ).build();

        final ApplyNodePermissionsParams applyNodePermissionsParams =
            ApplyNodePermissionsParams.create().nodeId( NodeId.from( "nodeId1" ) ).permissions( acl ).build();

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) )
            .thenReturn( Repository.create()
                             .id( RepositoryId.from( "com.enonic.cms.default" ) )
                             .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
                             .build() );

        Mockito.when( this.nodeService.applyPermissions( Mockito.any() ) )
            .thenReturn( ApplyNodePermissionsResult.create()
                             .addResult( applyNodePermissionsParams.getNodeId(), ContextAccessor.current().getBranch(),
                                         Node.create( applyNodePermissionsParams.getNodeId() ).build() )
                             .addResult( applyNodePermissionsParams.getNodeId(), ContentConstants.BRANCH_MASTER,
                                         Node.create( applyNodePermissionsParams.getNodeId() ).build() )
                             .addResult( NodeId.from( "nodeId2" ), ContextAccessor.current().getBranch(),
                                         Node.create( NodeId.from( "nodeId2" ) ).build() )
                             .build() );

        Mockito.when( this.nodeService.getByPath( Mockito.any() ) )
            .thenReturn( Node.create( applyNodePermissionsParams.getNodeId() ).build() );

        runScript( "/lib/xp/examples/node/applyPermissions.js" );
    }

}
