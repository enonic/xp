package com.enonic.xp.admin.impl.rest.resource.content.page;

import java.time.Instant;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.page.PageTemplates;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;

public class PageTemplateResourceTest
    extends AdminResourceTestSupport
{
    private PageTemplateService pageTemplateService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private SecurityService securityService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

    @Override
    protected Object getResourceInstance()
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
        pageTemplateService = Mockito.mock( PageTemplateService.class );
        securityService = Mockito.mock( SecurityService.class );
        siteService = Mockito.mock( SiteService.class );
        contentService = Mockito.mock( ContentService.class );

        final PageTemplateResource resource = new PageTemplateResource();
        resource.setPageTemplateService( pageTemplateService );
        resource.setContentService( contentService );
        resource.setContentTypeService( contentTypeService );
        resource.setSecurityService( securityService );
        resource.setSiteService( siteService );
        return resource;
    }

    @Test
    public void isRenderableFalse()
        throws Exception
    {
        Content content = createContent( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", "myapplication:content-type" );
        Mockito.when( this.contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( content );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "false", response );
    }

    @Test
    public void isRenderableContentNotFound()
        throws Exception
    {
        Content content = createContent( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", "myapplication:content-type" );
        Mockito.when( this.contentService.getById( Mockito.isA( ContentId.class ) ) ).thenThrow(
            new ContentNotFoundException( content.getId(), Branch.from( "draft" ) ) );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "false", response );
    }

    @Test
    public void isRenderableFragment()
        throws Exception
    {
        Content content = createContent( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", ContentTypeName.fragment().toString() );
        Mockito.when( this.contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( content );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "true", response );
    }

    @Test
    public void isRenderablePageTemplate()
        throws Exception
    {
        PageTemplate pageTemplate =
            createPageTemplate( "88811414-9967-4f59-a76e-5de250441e50", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.eq( pageTemplate.getId() ) ) ).thenReturn( pageTemplate );

        Site site = createSite( "8dcb8d39-e3be-4b2d-99dd-223666fc900c", "my-site", SiteConfigs.empty() );
        Mockito.when( contentService.getNearestSite( Mockito.eq( pageTemplate.getId() ) ) ).thenReturn( site );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", pageTemplate.getId().toString() ).
            get().getAsString();

        assertEquals( "true", response );
    }

    @Test
    public void isRenderablePageTemplateNoController()
        throws Exception
    {
        PageTemplate pageTemplate =
            createPageTemplate( "88811414-9967-4f59-a76e-5de250441e50", "content-name", "myapplication:content-type", null );
        Mockito.when( contentService.getById( Mockito.eq( pageTemplate.getId() ) ) ).thenReturn( pageTemplate );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", pageTemplate.getId().toString() ).
            get().getAsString();

        assertEquals( "false", response );
    }

    @Test
    public void isRenderableSupportedByPageTemplate()
        throws Exception
    {
        Content content = createContent( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );

        PageTemplate pageTemplate =
            createPageTemplate( "88811414-9967-4f59-a76e-5de250441e50", "content-name", "myapplication:content-type" );
        Mockito.when( pageTemplateService.getBySite( Mockito.isA( ContentId.class ) ) ).thenReturn( PageTemplates.from( pageTemplate ) );

        Site site = createSite( "8dcb8d39-e3be-4b2d-99dd-223666fc900c", "my-site", SiteConfigs.empty() );
        Mockito.when( contentService.getNearestSite( Mockito.eq( content.getId() ) ) ).thenReturn( site );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "true", response );
    }

    @Test
    public void isRenderableContentWithPageController()
        throws Exception
    {
        Content content =
            createContentWithPageController( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );

        Site site = createSite( "8dcb8d39-e3be-4b2d-99dd-223666fc900c", "my-site", SiteConfigs.empty() );
        Mockito.when( contentService.getNearestSite( Mockito.eq( content.getId() ) ) ).thenReturn( site );
        Mockito.when( pageTemplateService.getBySite( Mockito.isA( ContentId.class ) ) ).thenReturn( PageTemplates.empty() );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "true", response );
    }

    @Test
    public void isRenderableByControllerMapping()
        throws Exception
    {
        Content content = createContent( "83ac6e65-791b-4398-9ab5-ff5cab999036", "content-name", "myapplication:content-type" );
        Mockito.when( contentService.getById( Mockito.eq( content.getId() ) ) ).thenReturn( content );

        final SiteConfig siteConfig =
            SiteConfig.create().application( ApplicationKey.from( "myapplication" ) ).config( new PropertyTree() ).build();
        final SiteConfigs siteConfigs = SiteConfigs.from( siteConfig );
        Site site = createSite( "8dcb8d39-e3be-4b2d-99dd-223666fc900c", "my-site", siteConfigs );
        Mockito.when( contentService.getNearestSite( Mockito.eq( content.getId() ) ) ).thenReturn( site );
        Mockito.when( pageTemplateService.getBySite( Mockito.isA( ContentId.class ) ) ).thenReturn( PageTemplates.empty() );
        final ControllerMappingDescriptor mapingDescriptor = ControllerMappingDescriptor.create().
            contentConstraint( "type:'.*:content-type'" ).
            controller( ResourceKey.from( "myapplication:/some/path" ) ).
            build();
        final SiteDescriptor siteDescriptor =
            SiteDescriptor.create().mappingDescriptors( ControllerMappingDescriptors.from( mapingDescriptor ) ).build();
        Mockito.when( siteService.getDescriptor( Mockito.isA( ApplicationKey.class ) ) ).thenReturn( siteDescriptor );

        String response = request().path( "content/page/template/isRenderable" ).
            queryParam( "contentId", content.getId().toString() ).
            get().getAsString();

        assertEquals( "true", response );
    }

    @Test
    public void createPageTemplateWithExistingPathShouldIncrementCounter()
        throws Exception
    {
        ContentPath templatePath = ContentPath.from( "myapplication/_templates/template-myapplication" );
        ContentPath templatePath1 = ContentPath.from( "myapplication/_templates/template-myapplication-1" );
        ContentPath templatePath2 = ContentPath.from( "myapplication/_templates/template-myapplication-2" );
        Mockito.when( contentService.contentExists( Mockito.eq( templatePath ) ) ).thenReturn( true );
        Mockito.when( contentService.contentExists( Mockito.eq( templatePath1 ) ) ).thenReturn( true );
        Mockito.when( contentService.contentExists( Mockito.eq( templatePath2 ) ) ).thenReturn( false );

        Matcher<CreatePageTemplateParams> paramsMatcher =
            Matchers.hasProperty( "name", Matchers.equalTo( ContentName.from( "template-myapplication-2" ) ) );

        PageTemplate template = createPageTemplate( "template-id", "template-myapplication-2", "myapplication:content-type" );
        Mockito.when( pageTemplateService.create( Mockito.argThat( paramsMatcher ) ) ).thenReturn( template );

        String response = request().path( "content/page/template/create" ).
            entity( readFromFile( "create_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_template_success.json", response );
    }

    private PageTemplate createPageTemplate( final String id, final String name, final String canRender, final DescriptorKey controller )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "supports", canRender );

        final Page page = Page.create().
            controller( controller ).
            config( new PropertyTree() ).
            regions( PageRegions.create().build() ).
            build();

        return PageTemplate.newPageTemplate().
            canRender( ContentTypeNames.from( canRender ) ).
            id( ContentId.from( id ) ).
            name( name ).
            displayName( "My page template" ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            data( data ).
            type( ContentTypeName.pageTemplate() ).
            inheritPermissions( true ).
            parentPath( ContentPath.from( "/site/_templates" ) ).
            page( page ).
            build();

    }

    private PageTemplate createPageTemplate( final String id, final String name, final String canRender )
    {
        return createPageTemplate( id, name, canRender, DescriptorKey.from( "my-descriptor" ) );
    }

    private Content createContent( final String id, final ContentPath parentPath, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create().
            id( ContentId.from( id ) ).
            parentPath( parentPath ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
            publishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                to( Instant.parse( "2016-11-22T10:36:00Z" ) ).
                first( Instant.parse( "2016-11-02T10:36:00Z" ) ).
                build() ).
            build();
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        return this.createContent( id, ContentPath.ROOT, name, contentTypeName );
    }

    private Content createContentWithPageController( final String id, final String name, final String contentTypeName )
    {
        final Content content = this.createContent( id, ContentPath.ROOT, name, contentTypeName );
        final Page page = Page.create().
            controller( DescriptorKey.from( "my-descriptor" ) ).
            config( new PropertyTree() ).
            regions( PageRegions.create().build() ).
            build();
        return Content.create( content ).page( page ).build();
    }

    private Site createSite( final String id, final String name, SiteConfigs siteConfigs )
    {
        return Site.create().
            siteConfigs( siteConfigs ).
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( this.currentTime ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( this.currentTime ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.site() ).
            build();
    }
}