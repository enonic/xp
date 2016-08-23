package com.enonic.xp.branch;


import org.junit.Test;

import static org.junit.Assert.*;

public class BranchIdTest
{

    @Test
    public void fromString()
    {
        BranchId branchId = BranchId.from( "aaa" );
        assertEquals( "aaa", branchId.getValue() );
        assertEquals( "aaa", branchId.toString() );
        assertEquals( 96321, branchId.hashCode() );
    }

    @Test
    public void fromBuilder()
    {
        BranchId.Builder builder = BranchId.create();
        builder.value( "bbb" );
        BranchId branchId = builder.build();
        assertEquals( "bbb", branchId.getValue() );
        assertEquals( "bbb", branchId.toString() );
    }

    @Test
    public void compare()
    {
        BranchId branchId0 = BranchId.from( "aaa" );
        BranchId branchId1 = branchId0;
        BranchId branchId2 = BranchId.from( "aaa" );
        BranchId branchId3 = BranchId.from( "bbb" );
        BranchId branchId4 = null;
        Object branch5 = new Object();

        assertEquals( true, branchId0.equals( branchId1 ) );
        assertEquals( true, branchId0.equals( branchId2 ) );
        assertEquals( false, branchId0.equals( branchId3 ) );
        assertEquals( false, branchId0.equals( branchId4 ) );
        assertEquals( false, branchId0.equals( branch5 ) );
    }
}
