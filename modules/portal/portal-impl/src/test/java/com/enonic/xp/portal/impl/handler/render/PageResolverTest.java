package com.enonic.xp.portal.impl.handler.render;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageResolverTest
{
    private PropertyTree configA;

    private PropertyTree configB;

    private PageRegions regionsA;

    private PageRegions regionsB;

    @Mock
    PageTemplateService pageTemplateService;

    PageResolver pageResolver;

    @BeforeEach
    public void before()
    {
        configA = new PropertyTree();
        configA.addString( "a", "1" );

        configB = new PropertyTree();
        configB.addString( "b", "1" );

        regionsA = PageRegions.create()
            .add( Region.create().name( "regionA" ).add( PartComponent.create().descriptor( "myapp:my-part" ).build() ).build() )
            .build();

        regionsB = PageRegions.create()
            .add( Region.create().name( "regionB" ).add( PartComponent.create().descriptor( "myapp:my-part" ).build() ).build() )
            .build();

        pageResolver = new PageResolver( pageTemplateService );
    }

    @Test
    public void given_Content_without_Page_then_effective_Page_is_same_as_in_Template()
    {
        // setup
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build() )
            .build();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        // exercise
        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );

        final Page effectivePage = result.getEffectivePage();
        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsA, effectivePage.getRegions() );
        assertNull( effectivePage.getDescriptor() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( DescriptorKey.from( "myapp:my-descriptor" ), result.getController() );
    }

    @Test
    public void content_without_Page_and_Template_withoutPage()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .build();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        PageResolverResult result = pageResolver.resolve( RenderMode.EDIT, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertNull( effectivePage.getDescriptor() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
    }

    @Test
    public void content_without_Page_and_no_default_template()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( null );

        final WebException webExceptionInLive =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.LIVE, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInLive.getStatus() );
        assertEquals( webExceptionInLive.getMessage(), "No default template found for content" );

        final WebException webExceptionInInline =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.INLINE, content, site ) );
        assertEquals( HttpStatus.IM_A_TEAPOT, webExceptionInInline.getStatus() );

        final WebException webExceptionInPreview =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.PREVIEW, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInPreview.getStatus() );

        final PageResolverResult result = pageResolver.resolve( RenderMode.EDIT, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertNull( effectivePage );
        assertNull( result.getController() );
    }

    @Test
    public void content_with_Page_without_template_or_descriptor()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        final Page page = Page.create().build();

        Content content = Content.create().page( page ).parentPath( site.getPath() ).name( "my-content" ).build();

        final WebException webExceptionInLive =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.LIVE, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInLive.getStatus() );
        assertEquals( webExceptionInLive.getMessage(), "Content page has neither template nor descriptor" );

        final WebException webExceptionInInline =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.INLINE, content, site ) );
        assertEquals( HttpStatus.IM_A_TEAPOT, webExceptionInInline.getStatus() );

        final WebException webExceptionInPreview =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.PREVIEW, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInPreview.getStatus() );

        final PageResolverResult result = pageResolver.resolve( RenderMode.EDIT, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertNull( result.getController() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void given_Content_with_Page_without_regions_then_effective_Page_gets_regions_from_Template()
    {
        // setup
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build() )
            .canRender( ContentTypeNames.from( ContentTypeName.templateFolder() ) )
            .build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .type( ContentTypeName.templateFolder() )
            .page( Page.create().template( template.getKey() ).config( configB ).build() )
            .build();

        when( pageTemplateService.getByKey( template.getKey() ) ).thenReturn( template );

        // exercise
        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );

        final Page effectivePage = result.getEffectivePage();
        // verify
        assertEquals( regionsA, effectivePage.getRegions() );
        assertEquals( configB, effectivePage.getConfig() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertNull( effectivePage.getDescriptor() );
        assertEquals( DescriptorKey.from( "myapp:my-descriptor" ), result.getController() );
    }

    @Test
    public void given_Content_with_Page_without_config_then_effective_Page_gets_config_from_Template()
    {
        // setup
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build() )
            .canRender( ContentTypeNames.from( ContentTypeName.templateFolder() ) )
            .build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( Page.create().template( template.getKey() ).regions( regionsB ).build() )
            .type( ContentTypeName.templateFolder() )
            .build();

        when( pageTemplateService.getByKey( template.getKey() ) ).thenReturn( template );

        // exercise
        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );
        final Page effectivePage = result.getEffectivePage();

        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsB, effectivePage.getRegions() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertNull( effectivePage.getDescriptor() );
    }

    @Test
    public void page_with_own_descriptor()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        final Page page = Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configB ).build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertEquals( DescriptorKey.from( "myapp:my-descriptor" ), result.getController() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void content_with_Page_but_template_was_deleted_fallback_to_default()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .id( ContentId.from( "content-id" ) )
            .name( "my-content" )
            .page( Page.create().template( PageTemplateKey.from( "t-not-exists" ) ).build() )
            .type( ContentTypeName.templateFolder() )
            .build();

        final Page page = Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( page )
            .canRender( ContentTypeNames.from( ContentTypeName.templateFolder() ) )
            .build();

        when( pageTemplateService.getByKey( PageTemplateKey.from( "t-not-exists" ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), null ) );
        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( DescriptorKey.from( "myapp:my-descriptor" ), result.getController() );
    }

    @Test
    public void content_with_Page_but_template_was_deleted_fallback_to_default_not_found()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .id( ContentId.from( "content-id" ) )
            .name( "my-content" )
            .page( Page.create().template( PageTemplateKey.from( "t-not-exists" ) ).build() )
            .type( ContentTypeName.templateFolder() )
            .build();

        final WebException webExceptionInLive =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.LIVE, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInLive.getStatus() );
        assertEquals( webExceptionInLive.getMessage(), "Template [t-not-exists] is missing and no default template found for content" );

        final WebException webExceptionInInline =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.INLINE, content, site ) );
        assertEquals( HttpStatus.IM_A_TEAPOT, webExceptionInInline.getStatus() );

        final WebException webExceptionInPreview =
            assertThrows( WebException.class, () -> pageResolver.resolve( RenderMode.PREVIEW, content, site ) );
        assertEquals( HttpStatus.NOT_FOUND, webExceptionInPreview.getStatus() );

        final PageResolverResult result = pageResolver.resolve( RenderMode.EDIT, content, site );

        final Page effectivePage = result.getEffectivePage();

        assertSame( content.getPage(), effectivePage );
        assertNull( result.getController() );
    }

    @Test
    public void content_is_PageTemplate()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        final Page page =
            Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( page )
            .canRender( ContentTypeNames.from( ContentTypeName.folder() ) )
            .build();

        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, template, site );

        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertEquals( DescriptorKey.from( "myapp:my-descriptor" ), result.getController() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void content_is_Fragment()
    {
        Site site = Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();

        final Page page = Page.create().fragment( FragmentComponent.create().build() ).build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        PageResolverResult result = pageResolver.resolve( RenderMode.LIVE, content, site );
        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertNull( result.getController() );

        verifyNoInteractions( pageTemplateService );
    }
}
