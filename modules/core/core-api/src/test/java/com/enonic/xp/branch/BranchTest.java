package com.enonic.xp.branch;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BranchTest
{
    @Test
    void empty()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( "" ) );
    }

    @Test
    void starts_with_dot()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( ".myBranch" ) );
    }

    @Test
    void allowed_characters()
    {
        Branch.from( "my.branch-this:IS-my-branch" );
    }

    @Test
    void underscore_not_allowed()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( "my.branch-this:IS-my_branch" ) );
    }

    @Test
    void fromString()
    {
        Branch branch = Branch.from( "aaa" );
        assertEquals( "aaa", branch.getValue() );
        assertEquals( "aaa", branch.toString() );
        assertEquals( 96321, branch.hashCode() );
    }

    @Test
    void fromBuilder()
    {
        Branch.Builder builder = Branch.create();
        builder.value( "bbb" );
        Branch branch = builder.build();
        assertEquals( "bbb", branch.getValue() );
        assertEquals( "bbb", branch.toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( Branch.class ).withNonnullFields( "value" ).verify();
    }
}
