package com.enonic.xp.region;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentPathTest
{
    @Test
    public void from_throws_IllegalArgumentException_when_odd_number_of_path_elements()
    {
        assertThrows(IllegalArgumentException.class, () -> ComponentPath.from( "region[0]" ));
    }

    @Test
    public void tostring()
    {
        assertEquals( "/my-region/0", ComponentPath.from( "/my-region/0" ).toString() );
        assertEquals( "/my-region/1", ComponentPath.from( "my-region/1" ).toString() );
        assertEquals( "/my-other-region/0/my-region/1", ComponentPath.from( "my-other-region/0/my-region/1" ).toString() );
    }

    @Test
    public void removeFirstLevel()
    {
        assertEquals( "/my-region/1", ComponentPath.from( "my-other-region/0/my-region/1" ).removeFirstLevel().toString() );
        assertNull( ComponentPath.from( "my-region/0" ).removeFirstLevel() );
    }

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( ComponentPath.class ).withNonnullFields( "regionAndComponentList" ).verify();
    }
}
