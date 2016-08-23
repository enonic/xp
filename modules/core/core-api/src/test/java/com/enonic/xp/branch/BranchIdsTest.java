package com.enonic.xp.branch;


import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.*;

public class BranchIdsTest
{

    @Test
    public void fromArray()
    {
        BranchIds branchIds = BranchIds.from( BranchId.from( "aaa" ), BranchId.from( "bbb" ), BranchId.from( "ccc" ) );
        assertEquals( 3, branchIds.getSize() );
        assertEquals( BranchId.from( "aaa" ), branchIds.first() );
        assertEquals( true, branchIds.contains( BranchId.from( "aaa" ) ) );
        assertEquals( true, branchIds.contains( BranchId.from( "bbb" ) ) );
        assertEquals( true, branchIds.contains( BranchId.from( "ccc" ) ) );
    }

    @Test
    public void fromCollection()
    {
        ArrayList<BranchId> list = new ArrayList();
        list.add( BranchId.from( "aaa" ) );
        list.add( BranchId.from( "bbb" ) );
        list.add( BranchId.from( "ccc" ) );
        BranchIds branchIds = BranchIds.from( list );
        assertEquals( 3, branchIds.getSize() );
        assertEquals( BranchId.from( "aaa" ), branchIds.first() );
        assertEquals( true, branchIds.contains( BranchId.from( "aaa" ) ) );
        assertEquals( true, branchIds.contains( BranchId.from( "bbb" ) ) );
        assertEquals( true, branchIds.contains( BranchId.from( "ccc" ) ) );
    }

    @Test
    public void empty()
    {
        BranchIds branchIds = BranchIds.empty();
        assertEquals( 0, branchIds.getSize() );
    }
}
