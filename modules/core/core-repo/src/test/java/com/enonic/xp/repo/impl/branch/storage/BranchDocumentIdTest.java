package com.enonic.xp.repo.impl.branch.storage;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BranchDocumentIdTest
{
    @Test
    void from()
    {
        final BranchDocumentId from = BranchDocumentId.from( "a_b" );
        assertEquals( Branch.from("b"), from.getBranch() );
        assertEquals( NodeId.from( "a"), from.getNodeId() );
        assertEquals( "a_b",from.toString() );
    }

    @Test
    void from_ids()
    {
        final BranchDocumentId from = BranchDocumentId.from( NodeId.from( "a"), Branch.from("b") );
        assertEquals( Branch.from("b"), from.getBranch() );
        assertEquals( NodeId.from( "a"), from.getNodeId() );
        assertEquals( "a_b",from.toString() );
    }

    @Test
    void from_invalid()
    {
        assertThrows( IllegalArgumentException.class, () -> BranchDocumentId.from( "a_b_" ));
        assertThrows( IllegalArgumentException.class, () -> BranchDocumentId.from( "_b" ));
        assertThrows( IllegalArgumentException.class, () -> BranchDocumentId.from( "c" ));
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( BranchDocumentId.class ).withNonnullFields( "nodeId", "branch" ).verify();
    }
}
