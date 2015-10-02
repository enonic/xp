package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.HashMultimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ImageUrlParams;

import static org.junit.Assert.*;


public class ImageUrlBuilderTest
{
    private ImageUrlBuilder urlBuilder;

    private ImageUrlParams imageUrlParams;

    private Media media;

    @Before
    public void init()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        portalRequest.setBaseUri( "/portal" );
        portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        this.imageUrlParams = new ImageUrlParams().portalRequest( portalRequest ).scale( "testScale" );

        urlBuilder = new ImageUrlBuilder();
        urlBuilder.setParams( imageUrlParams );

        final Attachment attachment = Attachment.create().
            name( "attachmentName" ).
            mimeType( "attachmentMimeType" ).
            size( 1 ).
            build();

        media = Mockito.mock( Media.class );
        final ContentId contentId = ContentId.from( "testID" );
        Mockito.when( media.getId() ).thenReturn( contentId );
        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName" ) );
        Mockito.when( media.getMediaAttachment() ).thenReturn( attachment );

        final ContentService contentService = Mockito.mock( ContentService.class );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenReturn( media );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( media );
        Mockito.when( contentService.getBinaryKey( contentId, attachment.getBinaryReference() ) ).thenReturn( "binaryHash" );

        urlBuilder.contentService = contentService;
    }

    @Test
    public void testWithEmptyParams()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID:e57c6588d59c360d2464a5eabdaa24c78f7d1ed6/testScale/testName",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithFormatParam()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID:e57c6588d59c360d2464a5eabdaa24c78f7d1ed6/testScale/testName.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithSameFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName.png" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID:e57c6588d59c360d2464a5eabdaa24c78f7d1ed6/testScale/testName.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithDiffFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName.jpg" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID:e57c6588d59c360d2464a5eabdaa24c78f7d1ed6/testScale/testName.jpg.png",
                      stringBuilder.toString() );
    }
}
