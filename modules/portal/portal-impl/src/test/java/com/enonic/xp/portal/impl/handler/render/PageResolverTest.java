package com.enonic.xp.portal.impl.handler.render;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Iterators;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Mock
    PageDescriptorService pageDescriptorService;

    @Mock
    LayoutDescriptorService layoutDescriptorService;

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

        pageResolver = new PageResolver( pageTemplateService, pageDescriptorService, layoutDescriptorService );
    }

    @Test
    public void given_Content_without_Page_then_effective_Page_is_same_as_in_Template()
    {
        // setup
        final Site site = createSite();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build() )
            .build();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        // exercise
        PageResolverResult result = pageResolver.resolve( content, site.getPath() );

        final Page effectivePage = result.getEffectivePage();
        // verify
        assertEquals( configA, effectivePage.getConfig() );
        assertEquals( regionsA, effectivePage.getRegions() );
        assertNull( effectivePage.getDescriptor() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( ApplicationKey.from( "myapp" ), result.getApplicationKey() );
    }

    @Test
    public void content_without_Page_and_Template_withoutPage()
    {
        final Site site = createSite();

        PageTemplate template =
            PageTemplate.newPageTemplate().key( PageTemplateKey.from( "t-1" ) ).parentPath( site.getPath() ).name( "my-template" ).build();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "Template [my-template] has no page descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.PREVIEW ), HttpStatus.NOT_FOUND,
                                  "Template [my-template] has no page descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.INLINE ), HttpStatus.IM_A_TEAPOT,
                                  "Template [my-template] has no page descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.EDIT ), HttpStatus.IM_A_TEAPOT,
                                  "Template [my-template] has no page descriptor" );
    }

    @Test
    public void content_without_Page_and_no_default_template()
    {
        final Site site = createSite();

        Content content = Content.create().parentPath( site.getPath() ).name( "my-content" ).build();

        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( null );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "No default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.PREVIEW ), HttpStatus.NOT_FOUND,
                                  "No default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.INLINE ), HttpStatus.IM_A_TEAPOT,
                                  "No default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.EDIT ), HttpStatus.IM_A_TEAPOT,
                                  "No default template found for content" );
    }

    @Test
    public void content_with_Page_without_template_or_descriptor()
    {
        final Site site = createSite();
        final Page page = Page.create().build();
        final Content content = Content.create().page( page ).parentPath( site.getPath() ).name( "my-content" ).build();

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "Content page has neither template nor descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.PREVIEW ), HttpStatus.NOT_FOUND,
                                  "Content page has neither template nor descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.INLINE ), HttpStatus.IM_A_TEAPOT,
                                  "Content page has neither template nor descriptor" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.EDIT ), HttpStatus.IM_A_TEAPOT,
                                  "Content page has neither template nor descriptor" );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void given_Content_with_Page_without_regions_then_effective_Page_gets_regions_from_Template()
    {
        // setup
        final Site site = createSite();
        final DescriptorKey descriptor = DescriptorKey.from( "myapp:my-descriptor" );

        final PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().descriptor( descriptor ).config( configA ).regions( regionsA ).build() )
            .canRender( ContentTypeNames.from( ContentTypeName.templateFolder() ) )
            .build();

        final Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .type( ContentTypeName.templateFolder() )
            .page( Page.create().template( template.getKey() ).config( configB ).build() )
            .build();

        final PageDescriptor defaultPageDescriptor =
            PageDescriptor.create().config( Form.create().build() ).regions( RegionDescriptors.create().build() ).key( descriptor ).build();

        when( pageDescriptorService.getByKey( descriptor ) ).thenReturn( defaultPageDescriptor );
        when( pageTemplateService.getByKey( template.getKey() ) ).thenReturn( template );

        // exercise
        PageResolverResult result = pageResolver.resolve(  content, site.getPath() );

        final Page effectivePage = result.getEffectivePage();
        // verify
        assertEquals( regionsA, effectivePage.getRegions() );
        assertEquals( configB, effectivePage.getConfig() );
        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertNull( effectivePage.getDescriptor() );
        assertEquals( descriptor.getApplicationKey(), result.getApplicationKey() );
        assertEquals( defaultPageDescriptor, result.getPageDescriptor() );
    }

    @Test
    public void content_with_PageTemplate_that_has_no_controller()
    {
        final Site site = createSite();

        final PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( Page.create().config( configA ).regions( regionsA ).build() )
            .canRender( ContentTypeNames.from( ContentTypeName.templateFolder() ) )
            .build();

        final Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .type( ContentTypeName.templateFolder() )
            .page( Page.create().template( template.getKey() ).config( configB ).build() )
            .build();

        when( pageTemplateService.getByKey( template.getKey() ) ).thenReturn( template );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "Template [my-template] has no page descriptor" );
    }

    @Test
    public void given_Content_with_Page_without_config_then_effective_Page_gets_config_from_Template()
    {
        // setup
        final Site site = createSite();

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
        PageResolverResult result = pageResolver.resolve( content, site.getPath() );
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
        final Site site = createSite();
        final Page page = Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configB ).build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        PageResolverResult result = pageResolver.resolve( content, site.getPath() );

        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertEquals( ApplicationKey.from( "myapp" ), result.getApplicationKey() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void page_with_region_added_from_descriptor()
    {
        final Site site = createSite();

        final DescriptorKey descriptorKey = DescriptorKey.from( "myapp:my-descriptor" );
        final Region region2 = Region.create().name( "regionWithTextComponent" ).add( TextComponent.create().build() ).build();
        final Region region3 = Region.create().name( "regionWithLayoutComponent" ).add( LayoutComponent.create().build() ).build();
        final PageRegions pageRegions = PageRegions.create().add( region2 ).add( region3 ).build();
        final Page page = Page.create().descriptor( descriptorKey ).regions( pageRegions ).config( configB ).build();

        final Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        final RegionDescriptors regionDescriptors = RegionDescriptors.create()
            .add( RegionDescriptor.create().name( "emptyRegion" ).build() )
            .add( RegionDescriptor.create().name( "regionWithTextComponent" ).build() )
            .add( RegionDescriptor.create().name( "regionWithLayoutComponent" ).build() )
            .build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .key( descriptorKey )
            .regions( regionDescriptors )
            .config( Form.create().build() )
            .modifiedTime( Instant.now() )
            .build();

        when( pageDescriptorService.getByKey( Mockito.any( DescriptorKey.class ) ) ).thenReturn( pageDescriptor );

        PageResolverResult result = pageResolver.resolve( content, site.getPath() );

        final Page effectivePage = result.getEffectivePage();

        assertNotSame( page, effectivePage );
        assertEquals( ApplicationKey.from( "myapp" ), result.getApplicationKey() );
        assertEquals( 3, Iterators.size( effectivePage.getRegions().iterator() ) );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void content_with_Page_but_template_was_deleted_fallback_to_default()
    {
        final Site site = createSite();

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

        when( pageTemplateService.getByKey( PageTemplateKey.from( "t-not-exists" ) ) ).thenThrow( ContentNotFoundException.class );
        when( pageTemplateService.getDefault( notNull() ) ).thenReturn( template );

        PageResolverResult result = pageResolver.resolve( content, site.getPath() );

        final Page effectivePage = result.getEffectivePage();

        assertEquals( template.getKey(), effectivePage.getTemplate() );
        assertEquals( ApplicationKey.from( "myapp" ), result.getApplicationKey() );
    }

    @Test
    public void content_with_Page_but_template_was_deleted_fallback_to_default_not_found()
    {
        final Site site = createSite();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .id( ContentId.from( "content-id" ) )
            .name( "my-content" )
            .page( Page.create().template( PageTemplateKey.from( "t-not-exists" ) ).build() )
            .type( ContentTypeName.templateFolder() )
            .build();

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "Template [t-not-exists] is missing and no default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.PREVIEW ), HttpStatus.NOT_FOUND,
                                  "Template [t-not-exists] is missing and no default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.INLINE ), HttpStatus.IM_A_TEAPOT,
                                  "Template [t-not-exists] is missing and no default template found for content" );

        assertThrowsWebException( () -> pageResolver.resolve( content, site.getPath() ).getEffectivePageOrElseThrow(RenderMode.EDIT), HttpStatus.IM_A_TEAPOT,
                                  "Template [t-not-exists] is missing and no default template found for content" );
    }

    @Test
    public void content_is_PageTemplate()
    {
        final Site site = createSite();

        final Page page =
            Page.create().descriptor( DescriptorKey.from( "myapp:my-descriptor" ) ).config( configA ).regions( regionsA ).build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( page )
            .canRender( ContentTypeNames.from( ContentTypeName.folder() ) )
            .build();

        PageResolverResult result = pageResolver.resolve( template, site.getPath() );

        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertEquals( ApplicationKey.from( "myapp" ), result.getApplicationKey() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void content_is_PageTemplate_withNoController()
    {
        final Site site = createSite();

        final Page page =
            Page.create().config( configA ).regions( regionsA ).build();

        PageTemplate template = PageTemplate.newPageTemplate()
            .key( PageTemplateKey.from( "t-1" ) )
            .parentPath( site.getPath() )
            .name( "my-template" )
            .page( page )
            .canRender( ContentTypeNames.from( ContentTypeName.folder() ) )
            .build();

        assertThrowsWebException( () -> pageResolver.resolve( template, site.getPath() ).getEffectivePageOrElseThrow( RenderMode.LIVE ), HttpStatus.NOT_FOUND,
                                  "Template [my-template] has no page descriptor" );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void contentPageWithFragment()
    {
        final Site site = createSite();
        final Page page = Page.create().fragment( FragmentComponent.create().build() ).build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        PageResolverResult result = pageResolver.resolve( content, site.getPath() );
        final Page effectivePage = result.getEffectivePage();

        assertSame( page, effectivePage );
        assertNull( result.getApplicationKey() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void contentPageIsFragmentWithEmptyLayout()
    {
        final Site site = createSite();
        final LayoutComponent layoutComponent = LayoutComponent.create().build();
        final Page page = Page.create().fragment( layoutComponent ).build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        PageResolverResult result = pageResolver.resolve( content, site.getPath() );
        final Page effectivePage = result.getEffectivePage();

        assertNotSame( page, effectivePage );
        assertNull( result.getApplicationKey() );

        verifyNoInteractions( pageTemplateService );
    }

    @Test
    public void contentPageIsFragmentWithLayoutAndEmptyRegion()
    {
        final Site site = createSite();
        final DescriptorKey key = DescriptorKey.from( "someapp:sonelayout" );
        final LayoutComponent layoutComponent = LayoutComponent.create().descriptor( key ).build();
        final Page page = Page.create().fragment( layoutComponent ).build();

        final RegionDescriptors regionDescriptors =
            RegionDescriptors.create().add( RegionDescriptor.create().name( "main" ).build() ).build();
        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
            .regions( regionDescriptors )
            .modifiedTime( Instant.now() )
            .key( key )
            .config( Form.create().build() )
            .build();

        Content content = Content.create()
            .parentPath( site.getPath() )
            .name( "my-content" )
            .page( page )
            .type( ContentTypeName.templateFolder() )
            .build();

        when( layoutDescriptorService.getByKey( key ) ).thenReturn( layoutDescriptor );
        PageResolverResult result = pageResolver.resolve( content, site.getPath() );
        final Page effectivePage = result.getEffectivePage();

        verify( layoutDescriptorService, times( 1 ) ).getByKey( any( DescriptorKey.class ) );
        assertNotSame( page, effectivePage );
        assertNull( result.getApplicationKey() );

        final Component builtComponent = effectivePage.getFragment();
        assertInstanceOf( LayoutComponent.class, builtComponent );
        final LayoutComponent buitLayoutComponent = (LayoutComponent) builtComponent;
        assertTrue( buitLayoutComponent.hasRegions() );
        assertNotNull( buitLayoutComponent.getRegion( "main" ) );
    }

    @Test
    void contentIsShortcut()
    {
        final PageResolverResult result =
            pageResolver.resolve( Content.create().path( "/shortcut" ).type( ContentTypeName.shortcut() ).build(), ContentPath.ROOT );
        assertNull( result.getEffectivePage() );
    }

    private static Site createSite()
    {
        return Site.create()
            .id( ContentId.from( "site-id" ) )
            .path( ContentPath.from( "/site" ) )
            .displayName( "My Site" )
            .type( ContentTypeName.from( "portal:site" ) )
            .build();
    }

    private static void assertThrowsWebException( Executable executable, HttpStatus status, String message )
    {
        final WebException webException = assertThrows( WebException.class, executable );
        assertEquals( status, webException.getStatus() );
        assertEquals( message, webException.getMessage() );
    }
}
