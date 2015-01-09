package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final ServiceUrlBuilder builder = this.builders.serviceUrl().
            service( "myservice" ).
            param( "a", 3 );

        assertEquals( "/portal/live/stage/some/path/_/service/mymodule/myservice?a=3", builder.toString() );
    }
}
