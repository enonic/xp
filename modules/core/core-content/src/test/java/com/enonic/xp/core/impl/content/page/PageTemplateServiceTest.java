package com.enonic.xp.core.impl.content.page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplates;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PageTemplateServiceTest
{
    private ContentService contentService;

    private PageTemplateServiceImpl service;

    @BeforeEach
    public void setUp()
    {
        this.contentService = mock( ContentService.class );
        this.service = new PageTemplateServiceImpl( this.contentService );
    }

    @Test
    public void testCreate_withAllParameters()
    {
        final ContentTypeName supportedType = ContentTypeName.from( "com.myapp:supported-type" );
        final ContentTypeNames supports = ContentTypeNames.from( supportedType );

        final Region mainRegion = Region.create().name( "main" ).build();
        final Regions regions = Regions.create().add( mainRegion ).build();

        final CreatePageTemplateParams params = new CreatePageTemplateParams().site( ContentPath.from( "/site" ) )
            .name( ContentName.from( "my-full-template" ) )
            .displayName( "My Full Template" )
            .controller( DescriptorKey.from( "com.myapp:my-controller" ) )
            .supports( supports )
            .regions( regions )
            .pageConfig( new PropertyTree() );

        final PageTemplate mockTemplate = mock( PageTemplate.class );
        when( this.contentService.create( any( CreateContentParams.class ) ) ).thenReturn( mockTemplate );

        final PageTemplate result = this.service.create( params );

        assertNotNull( result );
        assertEquals( mockTemplate, result );

        final ArgumentCaptor<CreateContentParams> captor = ArgumentCaptor.forClass( CreateContentParams.class );
        verify( this.contentService ).create( captor.capture() );

        final CreateContentParams capturedParams = captor.getValue();

        assertEquals( "my-full-template", capturedParams.getName().toString() );
        assertEquals( "My Full Template", capturedParams.getDisplayName() );
        assertEquals( ContentTypeName.pageTemplate(), capturedParams.getType() );
        assertNotNull( capturedParams.getPage() );
        assertEquals( params.getController(), capturedParams.getPage().getDescriptor() );

        assertEquals( regions, capturedParams.getPage().getRegions() );

        final PropertyTree capturedData = capturedParams.getData();
        assertNotNull( capturedData );
        assertEquals( supportedType.toString(), capturedData.getString( "supports" ) );
    }

    @Test
    public void testGetByKey()
    {
        final ContentId contentId = ContentId.from( "some-id" );
        final PageTemplateKey key = PageTemplateKey.from( contentId );
        final PageTemplate mockTemplate = mock( PageTemplate.class );

        when( this.contentService.getById( contentId ) ).thenReturn( mockTemplate );

        final PageTemplate result = this.service.getByKey( key );

        assertNotNull( result );
        assertEquals( mockTemplate, result );
        verify( this.contentService ).getById( contentId );
    }

    @Test
    public void testGetByKey_nullKey()
    {
        final NullPointerException exception = assertThrows( NullPointerException.class, () -> this.service.getByKey( null ) );
        assertEquals( "pageTemplateKey is required", exception.getMessage() );
    }

    @Test
    public void testGetDefault()
    {
        final ContentId siteId = ContentId.from( "site-id" );
        final ContentPath sitePath = ContentPath.from( "/my-site" );
        final ContentTypeName contentType = ContentTypeName.from( "com.myapp:my-type" );

        final GetDefaultPageTemplateParams params = GetDefaultPageTemplateParams.create().site( siteId ).contentType( contentType ).build();

        final Content mockSite = mock( Content.class );
        when( mockSite.getPath() ).thenReturn( sitePath );
        when( this.contentService.getById( siteId ) ).thenReturn( mockSite );

        final PageTemplate mockTemplate = mock( PageTemplate.class );
        final FindContentByParentResult mockResult = mock( FindContentByParentResult.class );
        when( mockResult.getContents() ).thenReturn( Contents.from( mockTemplate ) );
        when( this.contentService.findByParent( any( FindContentByParentParams.class ) ) ).thenReturn( mockResult );

        final PageTemplate result = this.service.getDefault( params );

        assertNotNull( result );
        assertEquals( mockTemplate, result );

        verify( this.contentService ).getById( siteId );

        final ArgumentCaptor<FindContentByParentParams> captor = ArgumentCaptor.forClass( FindContentByParentParams.class );
        verify( this.contentService ).findByParent( captor.capture() );

        final FindContentByParentParams capturedParams = captor.getValue();
        assertEquals( 1, capturedParams.getSize() );
        assertEquals( "/my-site/_templates", capturedParams.getParentPath().toString() );
        assertNotNull( capturedParams.getQueryFilters() );
    }

    @Test
    public void testGetBySite()
    {
        final ContentId siteId = ContentId.from( "site-id" );
        final ContentPath sitePath = ContentPath.from( "/my-site" );

        final Content mockSite = mock( Content.class );
        when( mockSite.getPath() ).thenReturn( sitePath );
        when( this.contentService.getById( siteId ) ).thenReturn( mockSite );

        final PageTemplate mockTemplate1 = mock( PageTemplate.class );
        final PageTemplate mockTemplate2 = mock( PageTemplate.class );
        final FindContentByParentResult mockResult = mock( FindContentByParentResult.class );
        when( mockResult.getContents() ).thenReturn( Contents.from( mockTemplate1, mockTemplate2 ) );
        when( this.contentService.findByParent( any( FindContentByParentParams.class ) ) ).thenReturn( mockResult );

        final PageTemplates result = this.service.getBySite( siteId );

        assertNotNull( result );
        assertEquals( 2, result.getSize() );

        verify( this.contentService ).getById( siteId );

        final ArgumentCaptor<FindContentByParentParams> captor = ArgumentCaptor.forClass( FindContentByParentParams.class );
        verify( this.contentService ).findByParent( captor.capture() );
        assertEquals( "/my-site/_templates", captor.getValue().getParentPath().toString() );
    }

    @Test
    public void testGetBySite_nullSiteId()
    {
        final NullPointerException exception = assertThrows( NullPointerException.class, () -> this.service.getBySite( null ) );
        assertEquals( "siteId is required", exception.getMessage() );
    }
}
