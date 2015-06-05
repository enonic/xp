package com.enonic.xp.portal.impl.resource.service;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.Multimap;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.page.Page;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.PageTemplateKey;
import com.enonic.xp.content.page.region.ComponentName;
import com.enonic.xp.content.page.region.PartComponent;
import com.enonic.xp.content.page.region.Region;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.resource.base.ModuleBaseResourceTest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;

public class ServiceResourceTest
    extends ModuleBaseResourceTest
{
    protected ContentService contentService;

    protected SiteService siteService;

    private ControllerScript controllerScript;

    @Override
    protected void configure()
        throws Exception
    {
        configureModuleService();

        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.services.setControllerScriptFactory( controllerScriptFactory );

        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.fromDir( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        this.contentService = Mockito.mock( ContentService.class );
        this.siteService = Mockito.mock( SiteService.class );
        this.services.setContentService( this.contentService );
        this.services.setSiteService( this.siteService );
    }

    @Test
    public void executeScript()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/service/demo/test" );
        request.setQueryString( "a=b" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        final ArgumentCaptor<PortalResponse> jsResponse = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture(), jsResponse.capture() );

        final PortalRequest portalRequest = jsRequest.getValue();
        assertNotNull( portalRequest );
        assertEquals( "GET", portalRequest.getMethod() );
        assertEquals( RenderMode.LIVE, portalRequest.getMode() );

        final Multimap<String, String> params = portalRequest.getParams();
        assertNotNull( params );
        assertEquals( "b", params.get( "a" ).iterator().next() );
    }

    @Test
    public void verifyUriSet()
        throws Exception
    {
        final MockHttpServletRequest request = newGetRequest( "/master/path/to/content/_/service/demo/test" );
        request.setQueryString( "a=b" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        final ArgumentCaptor<PortalResponse> jsResponse = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture(), jsResponse.capture() );

        final PortalRequest portalRequest = jsRequest.getValue();

        assertEquals( "http://localhost/portal/master/path/to/content/_/service/demo/test?a=b", portalRequest.getUri() );
    }

    @Test
    public void executeScript_validSite()
        throws Exception
    {
        setupContentAndSite();

        final MockHttpServletRequest request = newGetRequest( "/master/site/somepath/content/_/service/demo/test" );
        final MockHttpServletResponse response = executeRequest( request );

        assertEquals( 200, response.getStatus() );

        final ArgumentCaptor<PortalRequest> jsRequest = ArgumentCaptor.forClass( PortalRequest.class );
        final ArgumentCaptor<PortalResponse> jsResponse = ArgumentCaptor.forClass( PortalResponse.class );
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture(), jsResponse.capture() );

        final PortalRequest portalRequest = jsRequest.getValue();
        assertNotNull( portalRequest.getModule() );
        assertNotNull( portalRequest.getSite() );
        assertNotNull( portalRequest.getContent() );
    }

    private void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "mymodule:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.siteService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "mymodule:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.newPageRegions().
                add( Region.newRegion().name( "main-region" ).
                    add( PartComponent.newPartComponent().name( ComponentName.from( "mypart" ) ).
                        build() ).
                    build() ).
                build();

            Page page = Page.newPage().
                template( PageTemplateKey.from( "my-page" ) ).
                regions( pageRegions ).
                config( rootDataSet ).
                build();
            content.page( page );
        }
        return content.build();
    }

    private Site createSite( final String id, final String path, final String contentTypeName )
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.addString( "property1", "value1" );

        Page page = Page.newPage().
            template( PageTemplateKey.from( "my-page" ) ).
            config( rootDataSet ).
            build();

        return Site.newSite().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }
}
