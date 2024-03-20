package com.enonic.xp.portal.impl.url;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ImageUrlBuilderTest
{
    private ImageUrlBuilder urlBuilder;

    private ImageUrlParams imageUrlParams;

    private Media media;

    @BeforeEach
    public void init()
    {

        final Attachment attachment = Attachment.create().
            name( "attachmentName" ).
            mimeType( "attachmentMimeType" ).
            size( 1 ).
            build();

        media = Mockito.mock( Media.class );
        final ContentId contentId = ContentId.from( "testID" );
        Mockito.when( media.getId() ).thenReturn( contentId );
        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName" ) );
        Mockito.when( media.getType() ).thenReturn( ContentTypeName.imageMedia() );
        Mockito.when( media.getMediaAttachment() ).thenReturn( attachment );
        Mockito.when( media.getPath() ).thenReturn( ContentPath.from( "context/path" ) );

        final ContentService contentService = Mockito.mock( ContentService.class );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenReturn( media );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( media );
        Mockito.when( contentService.getBinaryKey( contentId, attachment.getBinaryReference() ) ).thenReturn( "binaryHash" );

        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/site" );
        portalRequest.setContent( media );

        this.imageUrlParams = new ImageUrlParams().portalRequest( portalRequest ).scale( "testScale" );

        urlBuilder = new ImageUrlBuilder();
        urlBuilder.setParams( imageUrlParams );

        urlBuilder.contentService = contentService;
    }

    @Test
    public void testWithEmptyParams()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName",
                      stringBuilder.toString() );
    }

    @Test
    public void testPlusSignInNameConverted()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "test+Name.png" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/test+Name.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testSpacesInNameConverted()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "test Name.png" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/test%20Name.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithFormatParam()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithSameFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName.png" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithDiffFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( media.getName() ).thenReturn( ContentName.from( "testName.jpg" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.jpg.png",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithScale()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.scale( "block(310,175)" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/block-310-175/testName",
                      stringBuilder.toString() );
    }

    @Test
    public void testWithScale_spaces()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.scale( "block( 310, 175)" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/myproject/draft/context/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/block-310-175/testName",
                      stringBuilder.toString() );
    }
}
