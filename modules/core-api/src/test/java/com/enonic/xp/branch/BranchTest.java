package com.enonic.xp.branch;


import org.junit.Test;

import static org.junit.Assert.*;

public class BranchTest
{

    @Test
    public void fromString()
    {
        Branch branch = Branch.from( "aaa" );
        assertEquals( "aaa", branch.getName() );
        assertEquals( "aaa", branch.toString() );
        assertEquals( 96321, branch.hashCode() );
    }

    @Test
    public void fromBuilder()
    {
        Branch.Builder builder = Branch.create();
        builder.name( "bbb" );
        Branch branch = builder.build();
        assertEquals( "bbb", branch.getName() );
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
