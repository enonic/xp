package com.enonic.xp.repo.impl.elasticsearch.branch;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StoreBranchDocumentIdTest
{
    @Test
    public void from()
        throws Exception
    {
        final BranchDocumentId id = BranchDocumentId.from( "myBlobKey_myBranch" );
        assertEquals( "myBlobKey_myBranch", id.toString() );
        assertEquals( "myBlobKey", id.getNodeId().toString() );
        assertEquals( "myBranch", id.getBranch().getValue() );
    }

    @Test
    public void from_contains_underscore()
        throws Exception
    {
        final BranchDocumentId branchDocumentId = BranchDocumentId.from( "_a_myBranch" );

        assertEquals( "_a", branchDocumentId.getNodeId().toString() );
        assertEquals( "myBranch", branchDocumentId.getBranch().getValue() );
    }

    @Test
    public void from_missing_ws()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> BranchDocumentId.from( "myBlobKey_" ));
    }

    @Test
    public void from_missing_nodeId()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> BranchDocumentId.from( "_myBranch" ));
    }


    @Test
    public void from_missing_separator()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> BranchDocumentId.from( "myBlobKeymyBranch" ));
    }
}
