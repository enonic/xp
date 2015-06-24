package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.HashMultimap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ImageUrlParams;

import static org.junit.Assert.*;


public class ImageUrlBuilderTest

{
    private PortalUrlBuilder urlBuilder;

    private ImageUrlParams imageUrlParams;

    private Content content;


    @Before
    public void init()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setModule( ModuleKey.from( "mymodule" ) );
        portalRequest.setBaseUri( "/portal" );
        portalRequest.setContentPath( ContentPath.from( "context/path" ) );

        this.imageUrlParams = new ImageUrlParams().portalRequest( portalRequest ).scale( "testScale" );

        urlBuilder = new ImageUrlBuilder();
        urlBuilder.setParams( imageUrlParams );

        final ContentService contentService = Mockito.mock( ContentService.class );

        content = Mockito.mock( Content.class );
        Mockito.when( contentService.getByPath( Mockito.any() ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any() ) ).thenReturn( content );

        Mockito.when( content.getId() ).thenReturn( ContentId.from( "testID" ) );
        Mockito.when( content.getName() ).thenReturn( ContentName.from( "testName" ) );

        urlBuilder.contentService = contentService;
    }

    @Test
    public void testWithEmptyParams()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID/testScale/testName", stringBuilder.toString() );
    }

    @Test
    public void testWithFormatParam()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID/testScale/testName.png", stringBuilder.toString() );
    }

    @Test
    public void testWithSameFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( content.getName() ).thenReturn( ContentName.from( "testName.png" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID/testScale/testName.png", stringBuilder.toString() );
    }

    @Test
    public void testWithDiffFormat()
    {
        final StringBuilder stringBuilder = new StringBuilder( "test/" );
        imageUrlParams.format( "png" );

        Mockito.when( content.getName() ).thenReturn( ContentName.from( "testName.jpg" ) );

        urlBuilder.buildUrl( stringBuilder, HashMultimap.create() );
        assertEquals( "test/draft/context/path/_/image/testID/testScale/testName.jpg.png", stringBuilder.toString() );
    }
}
