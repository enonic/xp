package com.enonic.xp.module;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleVersionTest
{
    @Test
    public void from()
    {
        final ModuleVersion version = ModuleVersion.from( "1.2.0.beta" );

        assertEquals( version.getMajor(), 1 );
        assertEquals( version.getMinor(), 2 );
        assertEquals( version.getMicro(), 0 );
        assertEquals( version.getQualifier(), "beta" );
        assertEquals( version.toString(), "1.2.0.beta" );
        assertEquals( version.hashCode(), -2041385145 );
    }

    @Test
    public void testCompareTo()
    {
        final ModuleVersion version = ModuleVersion.from( "1.2.0.beta" );
        final ModuleVersion version2 = version;

        assertTrue( version.compareTo( version2 ) == 0 );
        assertTrue( version.compareTo( ModuleVersion.from( "1.2.0.beta" ) ) == 0 );
        assertTrue( version.compareTo( ModuleVersion.from( "0.2.45" ) ) > 0 );
        assertTrue( version.compareTo( ModuleVersion.from( "2.0.12.alpha" ) ) < 0 );
        assertTrue( version.compareTo( ModuleVersion.from( "1.3.0.beta" ) ) < 0 );
        assertTrue( version.compareTo( ModuleVersion.from( "1.2.35.beta" ) ) < 0 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromInvalid()
    {
        ModuleVersion.from( "3.7-SNAPSHOT" );
    }

    @Test
    public void testEquals()
    {
        final ModuleVersion version = ModuleVersion.from( "1.2.0.beta" );
        final ModuleVersion version2 = version;

        assertTrue( version.equals( version2 ) );
        assertTrue( version.equals( ModuleVersion.from( "1.2.0.beta" ) ) );

        assertFalse( version.equals( null ) );
        assertFalse( version.equals( new Object() ) );
    }

}
