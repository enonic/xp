package com.enonic.xp.branch;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BranchTest
{
    @Test
    public void empty()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  Branch.from( "" ) );
    }

    @Test
    public void starts_with_dot()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () ->  Branch.from( ".myBranch" ) );
    }

    @Test
    public void allowed_characters()
        throws Exception
    {
        Branch.from( "my.branch-this:IS_my-branch" );
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
    public void compare()
    {
        Branch branch0 = Branch.from( "aaa" );
        Branch branch1 = branch0;
        Branch branch2 = Branch.from( "aaa" );
        Branch branch3 = Branch.from( "bbb" );
        Branch branch4 = null;
        Object branch5 = new Object();

        assertEquals( true, branch0.equals( branch1 ) );
        assertEquals( true, branch0.equals( branch2 ) );
        assertEquals( false, branch0.equals( branch3 ) );
        assertEquals( false, branch0.equals( branch4 ) );
        assertEquals( false, branch0.equals( branch5 ) );
    }
}
