package com.enonic.wem.api.account.profile;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddressTest
{
    @Test
    public void testSetters()
    {
        final Address address = new Address();

        assertNull( address.getCountry() );
        address.setCountry( "country" );
        assertEquals( "country", address.getCountry() );

        assertNull( address.getIsoCountry() );
        address.setIsoCountry( "isoCountry" );
        assertEquals( "isoCountry", address.getIsoCountry() );

        assertNull( address.getIsoRegion() );
        address.setIsoRegion( "isoRegion" );
        assertEquals( "isoRegion", address.getIsoRegion() );

        assertNull( address.getLabel() );
        address.setLabel( "label" );
        assertEquals( "label", address.getLabel() );

        assertNull( address.getPostalAddress() );
        address.setPostalAddress( "postalAddress" );
        assertEquals( "postalAddress", address.getPostalAddress() );

        assertNull( address.getPostalCode() );
        address.setPostalCode( "postalCode" );
        assertEquals( "postalCode", address.getPostalCode() );

        assertNull( address.getRegion() );
        address.setRegion( "region" );
        assertEquals( "region", address.getRegion() );

        assertNull( address.getStreet() );
        address.setStreet( "street" );
        assertEquals( "street", address.getStreet() );
    }
}
