package com.enonic.xp.repo.impl.node;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DuplicateValueResolverTest
{

    @Test
    public void displayName_first_copy()
        throws Exception
    {
        assertEquals( "My Node copy", DuplicateValueResolver.displayName( "My Node" ) );
        assertEquals( "My Node copy", DuplicateValueResolver.displayName( "My Node " ) );
        assertEquals( "My Node Copy Node copy", DuplicateValueResolver.displayName( "My Node Copy Node" ) );
    }

    @Test
    public void displayName_second_copy()
        throws Exception
    {
        assertEquals( "My node copy 2", DuplicateValueResolver.displayName( "My node copy" ) );
        assertEquals( "My node copy 2", DuplicateValueResolver.displayName( "My node Copy" ) );
        assertEquals( "My node copy copy 2", DuplicateValueResolver.displayName( "My node copy copy" ) );
    }

    @Test
    public void displayName_third_copy()
        throws Exception
    {
        assertEquals( "My node copy 3", DuplicateValueResolver.displayName( "My node copy 2" ) );
    }

    @Test
    public void name_first_copy()
        throws Exception
    {
        assertEquals( "my-node-copy", DuplicateValueResolver.name( "my-node" ) );
        assertEquals( "mynode-copy", DuplicateValueResolver.name( "mynode" ) );
        assertEquals( "mynodecopy-copy", DuplicateValueResolver.name( "mynodecopy" ) );
    }

    @Test
    public void name_second_copy()
        throws Exception
    {
        assertEquals( "my-node-copy-2", DuplicateValueResolver.name( "my-node-copy" ) );
        assertEquals( "my-node-copy-copy-2", DuplicateValueResolver.name( "my-node-copy-copy" ) );
    }

    @Test
    public void name_third_copy()
        throws Exception
    {
        assertEquals( "my-node-copy-3", DuplicateValueResolver.name( "my-node-copy-2" ) );
    }

    @Test
    public void name_copy_in_name_copy()
        throws Exception
    {
        assertEquals( "my-node-copy-copy-2", DuplicateValueResolver.name( "my-node-copy-copy" ) );
    }

    @Test
    public void name_copy_in_between_name_copy()
        throws Exception
    {
        assertEquals( "my-node-copy-fisk-copy", DuplicateValueResolver.name( "my-node-copy-fisk" ) );
    }

    @Test
    public void fileName_first_copy()
        throws Exception
    {
        assertEquals( "my-node-copy.jpg", DuplicateValueResolver.fileName( "my-node.jpg" ) );
        assertEquals( "my.node-copy.jpg", DuplicateValueResolver.fileName( "my.node.jpg" ) );
        assertEquals( "mynode-copy.jpg", DuplicateValueResolver.fileName( "mynode.jpg" ) );
        assertEquals( "mynode.copy-copy.png", DuplicateValueResolver.fileName( "mynode.copy.png" ) );
    }

    @Test
    public void fileName_second_copy()
        throws Exception
    {
        assertEquals( "my-node-copy-2.png", DuplicateValueResolver.fileName( "my-node-copy.png" ) );
        assertEquals( "my.node-copy-copy-2.png", DuplicateValueResolver.fileName( "my.node-copy-copy.png" ) );
        assertEquals( "mynode.copy-copy-2.png", DuplicateValueResolver.fileName( "mynode.copy-copy.png" ) );
        assertEquals( "my-node.copy-copy-2.png", DuplicateValueResolver.fileName( "my-node.copy-copy.png" ) );
    }

}