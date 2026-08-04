package com.enonic.xp.portal.impl.filter;

import java.net.URL;
import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class FilterScriptImplTest
{
    private FilterScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    protected ContentService contentService;

    public FilterScriptImplTest()
    {
    }

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalRequest.setRawPath( "/site/project/branch/path" );
        this.portalResponse = PortalResponse.create().build();

        final Application application = Mockito.mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapplication" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        this.resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = invocation.getArgument( 0 );
            final URL resourceUrl = FilterScriptImplTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( resourceService, null, application );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.contentService = Mockito.mock( ContentService.class );

        this.factory = new FilterScriptFactoryImpl( scriptService, contentService );
    }

    @Test
    void testExecute()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/filter/simple.js", null );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testExecute_recordsScriptTraceAttribute()
    {
        this.portalRequest.setMethod( HttpMethod.GET );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "filterScript" );
        Tracer.trace( trace, () -> execute( "myapplication:/filter/simple.js", null ) );

        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
        assertEquals( "myapplication:/filter/simple.js", trace.get( "script" ) );
    }

    @Test
    void testNextFilter()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/filter/callnext.js", webHandlerChain );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testRemoveHeader()
        throws Exception
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalResponse = PortalResponse.create().header( "pleaseDontFail", "value" ).build();
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        execute( "myapplication:/filter/removeHeader.js", webHandlerChain );
        assertThat( this.portalResponse.getHeaders() ).doesNotContainKey( "pleaseDontFail" );
    }

    @Test
    void testNoFilterFunction()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        try
        {
            execute( "myapplication:/filter/nofilter.js", webHandlerChain );
            fail( "Expected exception" );
        }
        catch ( WebException e )
        {
            assertEquals( "Missing exported function 'filter' in filter script: myapplication:/filter/nofilter.js", e.getMessage() );
        }
    }

    @Test
    void testExecErrorHandling()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        final ResourceProblemException e =
            assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/filtererror.js", webHandlerChain ) );
        assertEquals( "myapplication:/filter/filtererror.js", e.getResource().toString() );
        assertEquals( 3, e.getLineNumber() );
        assertNotNull( e.getMessage() );
    }

    @Test
    void testDuplicatedNextCall()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        try
        {
            execute( "myapplication:/filter/duplicated_next_call.js", webHandlerChain );
            fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "myapplication:/filter/duplicated_next_call.js", e.getResource().toString() );
            assertEquals( "Filter 'next' function was called multiple times", e.getMessage() );
        }
    }

    @Test
    void testResourceException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( ResourceProblemException.class );

        Assertions.assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/callnext.js", webHandlerChain ) );

        Mockito.verify( webHandlerChain, Mockito.times( 1 ) ).handle( Mockito.any(), Mockito.any() );
    }

    @Test
    void testHandleException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( Exception.class );

        final ResourceProblemException exception = Assertions.assertThrows( ResourceProblemException.class,
                                                                            () -> execute( "myapplication:/filter/callnext.js",
                                                                                           webHandlerChain ) );

        assertEquals( "Error executing filter script: myapplication:/filter/callnext.js", exception.getMessage() );

        Mockito.verify( webHandlerChain, Mockito.times( 1 ) ).handle( Mockito.any(), Mockito.any() );
    }

    @Test
    void testRerouteOnContentPathChange()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/mysite/municipalities" );
        this.portalRequest.setContentPath( ContentPath.from( "/mysite/municipalities" ) );
        this.portalRequest.setRepositoryId( ProjectName.from( "myproject" ).getRepoId() );
        this.portalRequest.setBranch( Branch.from( "draft" ) );

        final Content content = newContent();
        when( contentService.getByPath( eq( ContentPath.from( "/mysite/municipalities/oslo" ) ) ) ).thenReturn( content );

        final Site site = newSite();
        when( contentService.findNearestSiteByPath( eq( content.getPath() ) ) ).thenReturn( site );

        execute( "myapplication:/filter/reroute.js", webHandlerChain );

        final ArgumentCaptor<WebRequest> requestCaptor = ArgumentCaptor.forClass( WebRequest.class );
        Mockito.verify( webHandlerChain ).handle( requestCaptor.capture(), Mockito.any() );

        final PortalRequest reroutedRequest = (PortalRequest) requestCaptor.getValue();
        assertEquals( "/site/myproject/draft/mysite/municipalities", reroutedRequest.getRawPath() );
        assertEquals( ContentPath.from( "/mysite/municipalities/oslo" ), reroutedRequest.getContentPath() );
        assertEquals( ProjectName.from( "myproject" ).getRepoId(), reroutedRequest.getRepositoryId() );
        assertEquals( Branch.from( "draft" ), reroutedRequest.getBranch() );
        assertEquals( content, reroutedRequest.getContent() );
        assertEquals( site, reroutedRequest.getSite() );
        assertEquals( "/site/myproject/draft/mysite", reroutedRequest.getContextPath() );
    }

    @Test
    void testRerouteToNonExistentContent()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/mysite/municipalities" );
        this.portalRequest.setContentPath( ContentPath.from( "/mysite/municipalities" ) );
        this.portalRequest.setRepositoryId( ProjectName.from( "myproject" ).getRepoId() );
        this.portalRequest.setBranch( Branch.from( "draft" ) );

        execute( "myapplication:/filter/reroute.js", webHandlerChain );

        final ArgumentCaptor<WebRequest> requestCaptor = ArgumentCaptor.forClass( WebRequest.class );
        Mockito.verify( webHandlerChain ).handle( requestCaptor.capture(), Mockito.any() );

        final PortalRequest reroutedRequest = (PortalRequest) requestCaptor.getValue();
        assertEquals( ContentPath.from( "/mysite/municipalities/oslo" ), reroutedRequest.getContentPath() );
        Assertions.assertNull( reroutedRequest.getContent() );
        Assertions.assertNull( reroutedRequest.getSite() );
    }

    @Test
    void testNoRerouteWhenContentPathUnchanged()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setRawPath( "/site/myproject/draft/mysite" );
        this.portalRequest.setContentPath( ContentPath.from( "/mysite" ) );

        execute( "myapplication:/filter/callnext.js", webHandlerChain );

        Mockito.verify( webHandlerChain ).handle( Mockito.any(), Mockito.any() );
        Mockito.verifyNoInteractions( contentService );
    }

    private Content newContent()
    {
        final Content.Builder<?> builder = Content.create();
        builder.id( ContentId.from( "c8da0c10-0002-4b68-b407-87412f3e45c8" ) );
        builder.name( "oslo" );
        builder.displayName( "Oslo" );
        builder.parentPath( ContentPath.from( "/mysite/municipalities" ) );
        builder.type( ContentTypeName.from( ApplicationKey.from( "com.enonic.test.app" ), "municipality" ) );
        builder.modifier( PrincipalKey.from( "user:system:admin" ) );
        builder.modifiedTime( Instant.ofEpochSecond( 0 ) );
        builder.creator( PrincipalKey.from( "user:system:admin" ) );
        builder.createdTime( Instant.ofEpochSecond( 0 ) );
        builder.data( new PropertyTree() );
        builder.permissions( AccessControlList.create()
                                 .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                                 .build() );
        return builder.build();
    }

    private Site newSite()
    {
        final Site.Builder site = Site.create();
        site.id( ContentId.from( "site0c10-0002-4b68-b407-87412f3e45c9" ) );
        site.data( new PropertyTree() );
        site.name( "mysite" );
        site.parentPath( ContentPath.ROOT );
        site.permissions( AccessControlList.create()
                              .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.EVERYONE ).build() )
                              .build() );
        return site.build();
    }

    protected final void execute( final String script, final WebHandlerChain webHandlerChain )
    {
        final FilterScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        this.portalResponse = controllerScript.execute( this.portalRequest, this.portalResponse, webHandlerChain );
    }
}
