package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeneralUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final GeneralUrlBuilder urlBuilder = builders.generalUrl().
            path( "some/path" );

        assertEquals( "/root/portal/some/path", urlBuilder.toString() );
    }
}

