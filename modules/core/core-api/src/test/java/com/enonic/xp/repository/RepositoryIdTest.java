package com.enonic.xp.repository;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepositoryIdTest
{
    @Test
    public void builtin()
        throws Exception
    {
        RepositoryId.from( "com.enonic.cms.default" );
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

    @Test
    public void firstCharacterUnderscoreCheck()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( "_abc" ));
    }

    @Test
    public void firstCharacterPointCheck()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( ".abc" ));
    }

    @Test
    public void emptyCheck()
        throws Exception
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( "" ));
    }

    @Test
    public void nullCheck()
        throws Exception
    {
        assertThrows(NullPointerException.class, () -> RepositoryId.from( null ));
    }
}
