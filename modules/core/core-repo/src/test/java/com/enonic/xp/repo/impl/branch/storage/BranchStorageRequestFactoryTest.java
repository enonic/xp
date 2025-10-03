package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BranchStorageRequestFactoryTest
{

    @Test
    public void create()
        throws Exception
    {
        final StoreRequest storeRequest = BranchStorageRequestFactory.create( NodeBranchEntry.create().
            nodeId( NodeId.from( "nodeId" ) ).
            nodePath( new NodePath( "/nodePath" ) ).
            nodeVersionId( NodeVersionId.from( "nodeVersionId" ) ).
            nodeVersionKey( NodeVersionKey.from( "nodeBlobKey", "indexConfigBlobKey", "accessControlBlobKey" ) ).
            timestamp( Instant.EPOCH ).
            build(), RepositoryId.from( "my-repo-id" ), Branch.from( "myBranch" ) );

        assertEquals( "nodeId_myBranch", storeRequest.getId() );
        assertEquals( "nodeVersionId", storeRequest.getParent() );
    }
}
