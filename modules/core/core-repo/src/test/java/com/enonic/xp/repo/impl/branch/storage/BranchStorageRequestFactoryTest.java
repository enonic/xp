package com.enonic.xp.repo.impl.branch.storage;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.Assert.*;

public class BranchStorageRequestFactoryTest
{

    @Test
    public void create()
        throws Exception
    {
        final StoreRequest storeRequest = BranchStorageRequestFactory.create( NodeBranchMetadata.create().
            nodeId( NodeId.from( "nodeId" ) ).
            nodePath( NodePath.create( "nodePath" ).build() ).
            nodeState( NodeState.DEFAULT ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
            build(), InternalContext.create().
            branch( Branch.from( "myBranch" ) ).
            repositoryId( RepositoryId.from( "myRepoId" ) ).
            build() );

        assertEquals( storeRequest.getId(), "nodeId_myBranch" );
        assertEquals( storeRequest.getParent(), "nodeId_nodeVersionId" );
        assertEquals( storeRequest.getRouting(), "nodeId" );
    }
}