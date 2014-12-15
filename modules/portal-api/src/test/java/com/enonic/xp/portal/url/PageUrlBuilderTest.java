package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class PageUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final PageUrlBuilder urlBuilder = builders.pageUrl();

        assertEquals( "/root/portal/live/stage/some/path", urlBuilder.toString() );
    }
}

