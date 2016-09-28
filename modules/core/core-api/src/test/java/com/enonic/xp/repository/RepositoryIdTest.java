package com.enonic.xp.repository;

import org.junit.Test;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;

public class RepositoryIdTest
{
    @Test
    public void builtin()
        throws Exception
    {
        RepositoryId.from( "cms-repo" );
        RepositoryId.from( "system-repo" );
    }

    @Test
    public void validNodeIdNodeName()
        throws Exception
    {
        final String allowedCharacters = "az09-:_.";
        NodeId.from( allowedCharacters );
        NodeName.from( allowedCharacters );
        RepositoryId.from( allowedCharacters );
    }

    @Test(expected = IllegalArgumentException.class)
    public void firstCharacterUnderscoreCheck()
        throws Exception
    {
        RepositoryId.from( "_abc" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void firstCharacterPointCheck()
        throws Exception
    {
        RepositoryId.from( ".abc" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCheck()
        throws Exception
    {
        RepositoryId.from( "" );
    }

    @Test(expected = NullPointerException.class)
    public void nullCheck()
        throws Exception
    {
        RepositoryId.from( null );
    }
}
