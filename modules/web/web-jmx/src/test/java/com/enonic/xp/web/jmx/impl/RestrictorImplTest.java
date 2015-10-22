package com.enonic.xp.web.jmx.impl;

import javax.management.ObjectName;

import org.jolokia.util.RequestType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RestrictorImplTest
{
    private RestrictorImpl restrictor;

    @Before
    public void setup()
    {
        this.restrictor = new RestrictorImpl();
    }

    @Test
    public void testRestrictor()
        throws Exception
    {
        final ObjectName name = ObjectName.getInstance( "test:type=test" );

        assertEquals( true, this.restrictor.isTypeAllowed( RequestType.READ ) );
        assertEquals( true, this.restrictor.isRemoteAccessAllowed( "localhost" ) );
        assertEquals( true, this.restrictor.isOriginAllowed( "localhost", true ) );
        assertEquals( true, this.restrictor.isAttributeReadAllowed( name, "prop" ) );
        assertEquals( false, this.restrictor.isAttributeWriteAllowed( name, "prop" ) );
        assertEquals( false, this.restrictor.isOperationAllowed( name, "op" ) );
    }
}
