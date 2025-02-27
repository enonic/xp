package com.enonic.xp.portal.impl.url;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ImageUrlBuilderTest
{
    private ImageUrlBuilder urlBuilder;

    private ImageUrlParams imageUrlParams;

    private Media media;

    @BeforeEach
    public void init()
    {

        final Attachment attachment = Attachment.create().name( "attachmentName" ).mimeType( "attachmentMimeType" ).size( 1 ).build();

        media = mock( Media.class );
        final ContentId contentId = ContentId.from( "testID" );
        when( media.getId() ).thenReturn( contentId );
        when( media.getName() ).thenReturn( ContentName.from( "testName" ) );
        when( media.getType() ).thenReturn( ContentTypeName.imageMedia() );
        when( media.getMediaAttachment() ).thenReturn( attachment );
        when( media.getPath() ).thenReturn( ContentPath.from( "/mysite/path" ) );
        when( media.getPermissions() ).thenReturn(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() ) );

        ContentService contentService = mock( ContentService.class );
        when( contentService.getByPath( ContentPath.from( "/mysite/path" ) ) ).thenReturn( media );
        when( contentService.getById( any() ) ).thenReturn( media );
        when( contentService.getBinaryKey( contentId, attachment.getBinaryReference() ) ).thenReturn( "binaryHash" );

        PortalRequest portalRequest = new PortalRequest();
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setRepositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) );
        portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/site" );
        portalRequest.setContent( media );
        portalRequest.setRawRequest( mock( HttpServletRequest.class ) );

        this.imageUrlParams = new ImageUrlParams().portalRequest( portalRequest ).scale( "testScale" );

        urlBuilder = new ImageUrlBuilder();
        urlBuilder.setParams( imageUrlParams );

        urlBuilder.contentService = contentService;
    }

    @Test
    public void testWithEmptyParams()
    {
        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName", url );
    }

    @Test
    public void testPlusSignInNameConverted()
    {
        when( media.getName() ).thenReturn( ContentName.from( "test+Name.png" ) );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/test+Name.png",
                      url );
    }

    @Test
    public void testSpacesInNameConverted()
    {
        when( media.getName() ).thenReturn( ContentName.from( "test Name.png" ) );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/test%20Name.png",
                      url );
    }

    @Test
    public void testWithFormatParam()
    {
        imageUrlParams.format( "png" );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.png",
                      url );
    }

    @Test
    public void testWithSameFormat()
    {
        imageUrlParams.format( "png" );

        when( media.getName() ).thenReturn( ContentName.from( "testName.png" ) );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.png",
                      url );
    }

    @Test
    public void testWithDiffFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "/site" );
        imageUrlParams.format( "png" );

        when( media.getName() ).thenReturn( ContentName.from( "testName.jpg" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals(
            "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/testScale/testName.jpg.png",
            stringBuilder.toString() );
    }

    @Test
    public void testWithScale()
    {
        imageUrlParams.scale( "block(310,175)" );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/block-310-175/testName",
                      url );
    }

    @Test
    public void testWithScale_spaces()
    {
        imageUrlParams.scale( "block( 310, 175)" );

        final String url = urlBuilder.build();
        assertEquals( "/site/myproject/draft/mysite/path/_/image/testID:2f6070713fd0e2823530379eb08b73c660e9a288/block-310-175/testName",
                      url );
    }
}
