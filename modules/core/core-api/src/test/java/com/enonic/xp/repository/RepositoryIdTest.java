package com.enonic.xp.repository;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryIdTest
{
    @Test
    void builtin()
    {
        RepositoryId.from( "com.enonic.cms.default" );
        RepositoryId.from( "system-repo" );
    }

    @Test
    void validNodeIdNodeName()
    {
        final String allowedCharacters = "az09-:_.";
        NodeId.from( allowedCharacters );
        NodeName.from( allowedCharacters );
        RepositoryId.from( allowedCharacters );
    }

    @Test
    void firstCharacterUnderscoreCheck()
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( "_abc" ));
    }

    @Test
    void firstCharacterPointCheck()
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( ".abc" ));
    }

    @Test
    void emptyCheck()
    {
        assertThrows(IllegalArgumentException.class, () -> RepositoryId.from( "" ));
    }

    @Test
    void nullCheck()
    {
        assertThrows(NullPointerException.class, () -> RepositoryId.from( null ));
    }
}
