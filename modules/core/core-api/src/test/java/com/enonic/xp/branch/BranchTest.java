package com.enonic.xp.branch;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BranchTest
{
    @Test
    public void empty()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( "" ) );
    }

    @Test
    public void starts_with_dot()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( ".myBranch" ) );
    }

    @Test
    public void allowed_characters()
    {
        Branch.from( "my.branch-this:IS-my-branch" );
    }

    @Test
    public void underscore_not_allowed()
    {
        assertThrows( IllegalArgumentException.class, () -> Branch.from( "my.branch-this:IS-my_branch" ) );
    }

    @Test
    public void fromString()
    {
        Branch branch = Branch.from( "aaa" );
        assertEquals( "aaa", branch.getValue() );
        assertEquals( "aaa", branch.toString() );
        assertEquals( 96321, branch.hashCode() );
    }

    @Test
    public void fromBuilder()
    {
        Branch.Builder builder = Branch.create();
        builder.value( "bbb" );
        Branch branch = builder.build();
        assertEquals( "bbb", branch.getValue() );
        assertEquals( "bbb", branch.toString() );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( Branch.class ).withNonnullFields( "value" ).verify();
    }
}
