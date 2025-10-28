package com.enonic.xp.app;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationKeyTest
{
    @Test
    void testCreate()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }

    @Test
    void testEquals()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
        assertEquals( ApplicationKey.from( "myapplication" ).hashCode(), applicationKey.hashCode() );
    }

    @Test
    void testFromApplicationKey()
    {
        assertEquals( "myapplication", ApplicationKey.from( "myapplication" ).getName() );
    }

    @Test
    void testParseApplicationVersion()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "myapplication" );

        assertEquals( ApplicationKey.from( "myapplication" ), applicationKey );
    }

    @Test
    void fromBundle()
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getSymbolicName() ).thenReturn( "myapplication" );
        ApplicationKey applicationKey = ApplicationKey.from( bundle );

        assertEquals( ApplicationKey.from( "myapplication" ).toString(), applicationKey.toString() );
    }

    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ApplicationKey.class ).withNonnullFields( "name" ).verify();
    }
}
