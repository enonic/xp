package com.enonic.xp.portal.impl.handler.render;

import org.junit.jupiter.api.BeforeEach;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.handler.BaseHandlerTest;
import com.enonic.xp.xml.parser.XmlPageDescriptorParser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class RenderBaseHandlerTest
    extends BaseHandlerTest
{
    protected PortalRequest request;

    protected ContentService contentService;

    protected PageTemplateService pageTemplateService;

    protected PageDescriptorService pageDescriptorService;

    protected LayoutDescriptorService layoutDescriptorService;

    protected ApplicationService applicationService;

    protected PortalUrlService portalUrlService;

    protected RendererDelegate rendererDelegate;

    protected PostProcessor postProcessor;

    private HttpServletRequest rawRequest;

    @BeforeEach
    void setupRenderBaseHandlerTest()
    {
        this.request = new PortalRequest();
        this.contentService = mock( ContentService.class );
        this.pageTemplateService = mock( PageTemplateService.class );
        this.pageDescriptorService = mock( PageDescriptorService.class );
        this.layoutDescriptorService = mock( LayoutDescriptorService.class );
        this.applicationService = mock( ApplicationService.class );
        this.portalUrlService = mock( PortalUrlService.class );

        this.rendererDelegate = mock( RendererDelegate.class );
        this.postProcessor = mock( PostProcessor.class );

        when( rendererDelegate.render( any(), same( request ) ) ).
            thenReturn( PortalResponse.create().body( "Ok" ).build() );

        this.rawRequest = mock( HttpServletRequest.class );
        when( this.rawRequest.isUserInRole( anyString() ) ).thenReturn( Boolean.TRUE );
        this.request.setRawRequest( this.rawRequest );
        this.request.setBaseUri( "/site" );
    }

    protected void setupSite()
    {
        final Site site = createSite( "id", "site" );
        when( this.contentService.getNearestSite( isA( ContentId.class ) ) ).thenReturn( site );
        when( this.contentService.findNearestSiteByPath( isA( ContentPath.class ) ) ).thenReturn( site );
    }

    protected void setupContent()
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );

        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    protected final void setupCustomizedTemplateContentAndSite()
    {
        Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );
        final PageDescriptor controllerDescriptor = createDescriptor();
        Page page = Page.create( content.getPage() ).template( null ).descriptor( controllerDescriptor.getKey() ).build();
        content = Content.create( content ).page( page ).build();

        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        when( this.contentService.getNearestSite( isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site" ) );

        when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    protected final void setupContentWithoutPage()
    {
        when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( createPage( "id", "site/somepath/content", "myapplication:ctype", false ) );

        final Site site = createSite( "id", "site" );

        when( this.contentService.findNearestSiteByPath( isA( ContentPath.class ) ) ).
            thenReturn( site );
    }

    protected final void setupTemplates()
    {
        when( this.pageTemplateService.getByKey( eq( PageTemplateKey.from( "my-page" ) ) ) ).thenReturn(
            createPageTemplate() );

        when( this.pageDescriptorService.getByKey( isA( DescriptorKey.class ) ) ).thenReturn( createDescriptor() );

    }

    protected final void setupController()
    {
        when( this.pageDescriptorService.getByKey( isA( DescriptorKey.class ) ) ).thenReturn( createDescriptor() );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) )
            .permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create().
                add( Region.create().name( "main-region" ).
                    add( PartComponent.create().descriptor( "myapp:mypart" ).
                        build() ).
                    build() ).
                build();

            Page page = Page.create().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.create().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( "portal:site" ) ).
            page( page ).
            build();
    }

    private PageTemplate createPageTemplate()
    {
        final PropertyTree pageTemplateConfig = new PropertyTree();
        pageTemplateConfig.addLong( "pause", 10000L );

        PageRegions pageRegions = PageRegions.create().
            add( Region.create().name( "main-region" ).
                add( PartComponent.create().descriptor( "myapp:mypart" ).
                    build() ).
                build() ).
            build();

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abc" ) ).
            canRender( ContentTypeNames.from( "myapplication:article", "myapplication:banner", "myapplication:ctype" ) ).
            regions( pageRegions ).
            config( pageTemplateConfig );

        builder.controller( DescriptorKey.from( "mainapplication:landing-page" ) );

        builder.displayName( "Main page template" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );

        return builder.build();
    }

    private PageDescriptor createDescriptor()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( "mainapplication" );
        final String name = "mypage";
        final DescriptorKey key = DescriptorKey.from( applicationKey, name );

        final String xml =
            "<?xml version=\"1.0\"?>\n" + "<page>\n" + "  <display-name>Landing page</display-name>\n" + "  <config/>\n" + "</page>";
        final PageDescriptor.Builder builder = PageDescriptor.create();

        parseXml( applicationKey, builder, xml );

        return builder.
            key( key ).
            displayName( "Landing page" ).
            build();
    }

    private void parseXml( final ApplicationKey applicationKey, final PageDescriptor.Builder builder, final String xml )
    {
        final XmlPageDescriptorParser parser = new XmlPageDescriptorParser();
        parser.builder( builder );
        parser.currentApplication( applicationKey );
        parser.source( xml );
        parser.parse();
    }

    protected final void setRendererResult( final PortalResponse response )
    {
        when( this.rendererDelegate.render( any(), any() ) ).thenReturn( response );
    }
}
