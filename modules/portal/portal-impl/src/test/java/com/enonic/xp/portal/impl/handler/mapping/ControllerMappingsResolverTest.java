package com.enonic.xp.portal.impl.handler.mapping;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;

import static org.junit.Assert.*;

public class ControllerMappingsResolverTest
{
    private ContentService contentService;

    private SiteService siteService;

    private PortalRequest request;

    @Before
    public final void setup()
        throws Exception
    {
        final HttpServletRequest rawRequest = Mockito.mock( HttpServletRequest.class );
        Mockito.when( rawRequest.getRequestURI() ).thenReturn( "/portal/master/mysite/landing-page" );

        this.request = new PortalRequest();
        this.request.setMode( RenderMode.LIVE );
        this.request.setRawRequest( rawRequest );

        this.contentService = Mockito.mock( ContentService.class );
        this.siteService = Mockito.mock( SiteService.class );
    }

    @Test
    public void testNoDescriptors()
        throws Exception
    {
        final Content content = newContent();
        final Site site = newSite();
        this.request.setContentPath( content.getPath() );

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNull( mapping );
    }

    @Test
    public void testResolve()
        throws Exception
    {
        final Content content = newContent();
        final Site site = newSite();

        this.request.setContentPath( content.getPath() );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );
        final SiteDescriptor siteDescriptor = newSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( getAppKey() ) ).thenReturn( siteDescriptor );
        final SiteDescriptor siteDescriptor2 = newSiteDescriptor2();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor2 );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNotNull( mapping );
        assertEquals( "/site/controllers/controller2.js", mapping.getController().getPath() );
    }

    @Test
    public void testResolveWithParameters()
        throws Exception
    {
        final Content content = newContent();
        final Site site = newSite();

        this.request.setContentPath( ContentPath.from( content.getPath(), "api" ) );
        this.request.getParams().put( "key", "123" );
        this.request.getParams().put( "category", "foo" );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );
        final SiteDescriptor siteDescriptor = newSiteDescriptor3();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final boolean mappingCanHandleRequest = resolver.canHandle( request );
        final boolean mappingCanHandleRequest2 = resolver.canHandle( request );

        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertTrue( mappingCanHandleRequest );
        assertTrue( mappingCanHandleRequest2 );
        assertNotNull( mapping );
        assertEquals( "/other/controller1.js", mapping.getController().getPath() );
    }

    @Test
    public void testResolvePatternWithParametersNoMatch()
        throws Exception
    {
        final Content content = newContent();
        final Site site = newSite();

        this.request.setContentPath( ContentPath.from( content.getPath(), "api" ) );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );
        final SiteDescriptor siteDescriptor = newSiteDescriptor3();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final boolean mappingCanHandleRequest = resolver.canHandle( request );

        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertFalse( mappingCanHandleRequest );
        assertNull( mapping );
    }


    @Test
    public void testResolveUrlEditModeWithContentId()
        throws Exception
    {
        final Content content = newContent();
        final Site site = newSite();

        this.request.setContentPath( ContentPath.from( "/c8da0c10-0002-4b68-b407-87412f3e45c8" ) );
        this.request.setMode( RenderMode.EDIT );
        Mockito.when( this.contentService.getById( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );
        final SiteDescriptor siteDescriptor = newSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( getAppKey() ) ).thenReturn( siteDescriptor );
        final SiteDescriptor siteDescriptor2 = newSiteDescriptor2();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor2 );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNotNull( mapping );
        assertEquals( "/site/controllers/controller2.js", mapping.getController().getPath() );
    }

    @Test
    public void testResolveUrlAdminMode()
        throws Exception
    {
        final Content content = newContent();
        this.request.setContentPath( content.getPath() );
        this.request.setMode( RenderMode.ADMIN );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNull( mapping );
    }

    @Test
    public void testResolveContentNotInSite()
        throws Exception
    {
        final Content content = newContent();

        this.request.setContentPath( content.getPath() );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( null );
        final SiteDescriptor siteDescriptor = newSiteDescriptor();
        Mockito.when( this.siteService.getDescriptor( getAppKey() ) ).thenReturn( siteDescriptor );
        final SiteDescriptor siteDescriptor2 = newSiteDescriptor2();
        Mockito.when( this.siteService.getDescriptor( getAppKey2() ) ).thenReturn( siteDescriptor2 );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNull( mapping );
    }

    @Test
    public void testResolveUrlWithEndPoint()
        throws Exception
    {
        final Content content = newFragmentContent();
        final Site site = newSite();

        this.request.setContentPath( content.getPath() );
        this.request.setEndpointPath( "/_/image/1234:5678/width-695/superhero_5.jpg" );
        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.getNearestSite( content.getId() ) ).thenReturn( site );
        final SiteDescriptor siteDescriptor4 = newDescriptorForFragments();
        Mockito.when( this.siteService.getDescriptor( getAppKey() ) ).thenReturn( siteDescriptor4 );

        final ControllerMappingsResolver resolver = new ControllerMappingsResolver( this.siteService, this.contentService );
        final ControllerMappingDescriptor mapping = resolver.resolve( request );

        assertNull( mapping );
    }

    private SiteDescriptor newSiteDescriptor()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey(), "/site/controllers/controller1.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "_id:'123456'" ).
            order( 10 ).
            build();
        final ControllerMappingDescriptor mapping2 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey(), "/site/controllers/controller2.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "_path:'/mysite/landing-page'" ).
            order( 5 ).
            build();
        final ControllerMappingDescriptor mapping3 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey(), "/site/controllers/controller3.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "_name:'landing-page'" ).
            order( 15 ).
            build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1, mapping2, mapping3 );
        return SiteDescriptor.create().
            mappingDescriptors( mappings ).
            build();
    }


    private SiteDescriptor newSiteDescriptor2()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "_id:'123456'" ).
            order( 10 ).
            build();
        final ControllerMappingDescriptor mapping2 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey2(), "/other/controller2.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "_path:'/mysite/landing-page'" ).
            order( 5 ).
            build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1, mapping2 );
        return SiteDescriptor.create().
            mappingDescriptors( mappings ).
            build();
    }

    private SiteDescriptor newSiteDescriptor3()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey2(), "/other/controller1.js" ) ).
            pattern( "/.*api.*\\?category=.*&key=\\d+" ).
            order( 10 ).
            build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );
        return SiteDescriptor.create().
            mappingDescriptors( mappings ).
            build();
    }

    private SiteDescriptor newDescriptorForFragments()
    {
        final ControllerMappingDescriptor mapping1 = ControllerMappingDescriptor.create().
            controller( ResourceKey.from( getAppKey(), "/site/controllers/controller1.js" ) ).
            pattern( "/.*" ).
            contentConstraint( "type:'portal:fragment'" ).
            order( 10 ).
            build();
        final ControllerMappingDescriptors mappings = ControllerMappingDescriptors.from( mapping1 );
        return SiteDescriptor.create().
            mappingDescriptors( mappings ).
            build();
    }

    private Content newContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "123456" ) );
        builder.name( "landing-page" );
        builder.displayName( "My Landing Page" );
        builder.parentPath( ContentPath.from( "/mysite" ) );
        builder.type( ContentTypeName.from( getAppKey(), "landing-page" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        return builder.build();
    }

    private Content newFragmentContent()
    {
        final Content.Builder builder = Content.create();
        builder.id( ContentId.from( "897546" ) );
        builder.name( "my-fragment" );
        builder.displayName( "My fragment" );
        builder.parentPath( ContentPath.from( "/mysite/fragment" ) );
        builder.type( ContentTypeName.fragment() );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        return builder.build();
    }

    private Site newSite()
    {
        final SiteConfig siteConfig = SiteConfig.create().
            application( getAppKey() ).
            config( new PropertyTree() ).
            build();

        final SiteConfig siteConfig2 = SiteConfig.create().
            application( getAppKey2() ).
            config( new PropertyTree() ).
            build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "100123" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig, siteConfig2 ) );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }

    private ApplicationKey getAppKey()
    {
        return ApplicationKey.from( "com.enonic.test.app" );
    }

    private ApplicationKey getAppKey2()
    {
        return ApplicationKey.from( "com.enonic.test.otherapp" );
    }
}