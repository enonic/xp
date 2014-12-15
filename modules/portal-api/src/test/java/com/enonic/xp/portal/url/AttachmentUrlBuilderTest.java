package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class AttachmentUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl();

        assertEquals( "/root/portal/live/stage/some/path", urlBuilder.toString() );
    }
}

