package com.enonic.xp.repo.impl.branch.storage;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchDocumentIdTest
{
    @Test
    void from_ids()
    {
        assertEquals( "a_b", BranchDocumentId.asString( NodeId.from( "a" ), Branch.from( "b" ) ) );
    }
}
