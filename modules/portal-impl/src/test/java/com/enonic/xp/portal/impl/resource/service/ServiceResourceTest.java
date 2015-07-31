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
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.controller.ControllerScript;
import com.enonic.xp.portal.impl.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.resource.base.ApplicationBaseResourceTest;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;

import static org.junit.Assert.*;

public class ServiceResourceTest
    extends ApplicationBaseResourceTest
{
    protected ContentService contentService;

    private ControllerScript controllerScript;

    @Override
    protected void configure()
        throws Exception
    {
        configureApplicationService();

        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        this.services.setControllerScriptFactory( controllerScriptFactory );

        this.controllerScript = Mockito.mock( ControllerScript.class );
        Mockito.when( controllerScriptFactory.fromDir( Mockito.anyObject() ) ).thenReturn( this.controllerScript );

        final PortalResponse portalResponse = PortalResponse.create().build();
        Mockito.when( this.controllerScript.execute( Mockito.anyObject() ) ).thenReturn( portalResponse );

        this.contentService = Mockito.mock( ContentService.class );
        this.services.setContentService( this.contentService );
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
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture() );

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
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture() );

        final PortalRequest portalRequest = jsRequest.getValue();

        assertEquals( "http://localhost/portal/master/path/to/content/_/service/demo/test?a=b", portalRequest.getUri() );
        assertEquals( "http://localhost/portal/master", portalRequest.getBaseUrl() );
        assertEquals( "http://localhost", portalRequest.getServerUrl() );
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
        Mockito.verify( this.controllerScript ).execute( jsRequest.capture() );

        final PortalRequest portalRequest = jsRequest.getValue();
        assertNotNull( portalRequest.getApplicationKey() );
        assertNotNull( portalRequest.getSite() );
        assertNotNull( portalRequest.getContent() );
    }

    private void setupContentAndSite()
        throws Exception
    {
        final Content content = createPage( "id", "site/somepath/content", "myapplication:ctype", true );

        Mockito.when( this.contentService.getByPath( ContentPath.from( "site/somepath/content" ).asAbsolute() ) ).
            thenReturn( content );

        Mockito.when( this.contentService.getNearestSite( Mockito.isA( ContentId.class ) ) ).
            thenReturn( createSite( "id", "site", "myapplication:contenttypename" ) );

        Mockito.when( this.contentService.getById( content.getId() ) ).
            thenReturn( content );
    }

    private Content createPage( final String id, final String path, final String contentTypeName, final boolean withPage )
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.addString( "property1", "value1" );

        final Content.Builder content = Content.create().
            id( ContentId.from( id ) ).
            path( ContentPath.from( path ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) );

        if ( withPage )
        {
            PageRegions pageRegions = PageRegions.create().
                add( Region.create().name( "main-region" ).
                    add( PartComponent.create().name( ComponentName.from( "mypart" ) ).
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

    private Site createSite( final String id, final String path, final String contentTypeName )
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
            type( ContentTypeName.from( contentTypeName ) ).
            page( page ).
            build();
    }
}
