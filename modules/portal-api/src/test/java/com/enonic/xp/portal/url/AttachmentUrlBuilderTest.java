package com.enonic.xp.portal.url;

import org.junit.Test;

import static org.junit.Assert.*;

public class AttachmentUrlBuilderTest
    extends AbstractUrlBuilderTest
{
    @Test
    public void createUrl_with_nothing_specified()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl();

        assertEquals( "/portal/stage/some/path/_/attachment", urlBuilder.toString() );
    }
    
    @Test
    public void createUrl_with_name()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl().name( "mycv.pdf" );

        assertEquals( "/portal/stage/some/path/_/attachment/mycv.pdf", urlBuilder.toString() );
    }

    @Test
    public void createUrl_with_label()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl().label( "source" );

        assertEquals( "/portal/stage/some/path/_/attachment/source", urlBuilder.toString() );
    }

    @Test
    public void createUrl_with_mediaId_and_nothing_specified()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl().mediaId( "123abc" );

        assertEquals( "/portal/stage/some/path/_/attachment/id/123abc", urlBuilder.toString() );
    }

    @Test
    public void createUrl_with_mediaId_and_name()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl().mediaId( "123abc" ).name( "mycv.pdf" );

        assertEquals( "/portal/stage/some/path/_/attachment/id/123abc/mycv.pdf", urlBuilder.toString() );
    }

    @Test
    public void createUrl_with_mediaId_and_label()
    {
        final AttachmentUrlBuilder urlBuilder = builders.attachmentUrl().mediaId( "123abc" ).label( "source" );

        assertEquals( "/portal/stage/some/path/_/attachment/id/123abc/source", urlBuilder.toString() );
    }
}

