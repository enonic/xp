package com.enonic.xp.branch;


import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BranchesTest
{

    @Test
    public void fromArray()
    {
        Branches branches = Branches.from( Branch.from( "aaa" ), Branch.from( "bbb" ), Branch.from( "ccc" ) );
        assertEquals( 3, branches.getSize() );
        assertEquals( Branch.from( "aaa" ), branches.first() );
        assertEquals( true, branches.contains( Branch.from( "aaa" ) ) );
        assertEquals( true, branches.contains( Branch.from( "bbb" ) ) );
        assertEquals( true, branches.contains( Branch.from( "ccc" ) ) );
    }

    @Test
    public void fromCollection()
    {
        ArrayList<Branch> list = new ArrayList();
        list.add( Branch.from( "aaa" ) );
        list.add( Branch.from( "bbb" ) );
        list.add( Branch.from( "ccc" ) );
        Branches branches = Branches.from( list );
        assertEquals( 3, branches.getSize() );
        assertEquals( Branch.from( "aaa" ), branches.first() );
        assertEquals( true, branches.contains( Branch.from( "aaa" ) ) );
        assertEquals( true, branches.contains( Branch.from( "bbb" ) ) );
        assertEquals( true, branches.contains( Branch.from( "ccc" ) ) );
    }

    @Test
    public void empty()
    {
        Branches branches = Branches.empty();
        assertEquals( 0, branches.getSize() );
    }
}
