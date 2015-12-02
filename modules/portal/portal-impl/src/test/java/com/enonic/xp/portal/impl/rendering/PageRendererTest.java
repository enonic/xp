package com.enonic.xp.portal.impl.rendering;

import java.time.Instant;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.filter.FilterChainResolver;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.postprocess.TestPostProcessInjection;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.filter.FilterDescriptors;

import static org.junit.Assert.*;

public class PageRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private Content content;

    private PageRenderer renderer;


    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create().
            id( ContentId.from( id ) ).
            parentPath( ContentPath.ROOT ).
            name( name ).
            valid( true ).
            createdTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            displayName( "My Content" ).
            modifiedTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( ContentTypeName.from( contentTypeName ) ).
            addExtraData( new ExtraData( MixinName.from( "myApplication:myField" ), metadata ) ).
            build();
    }

    private String configureEmptyComponent( RenderMode mode )
    {
        // setup
        portalRequest.setMode( mode );
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        renderer = new PageRenderer();

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInjection( new TestPostProcessInjection() );
        renderer.setPostProcessor( postProcessor );

        final FilterChainResolver resolver = Mockito.mock( FilterChainResolver.class );
        Mockito.when( resolver.resolve( this.portalRequest ) ).thenReturn( FilterDescriptors.empty() );
        renderer.setFilterChainResolver( resolver );

        // exercise
        portalResponse = renderer.render( content, portalRequest );

        return portalResponse.getAsString();
    }

    @Before
    public void before()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
        this.portalResponse = PortalResponse.create().build();
        this.portalRequest.setMode( RenderMode.EDIT );
    }

    @Test
    public void contentWithRenderModeEdit()
    {
        // setup
        configureEmptyComponent( RenderMode.EDIT );

        // exercise
        portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response =
            "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body data-portal-component-type=\"page\"></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    @Test
    public void contentWithRenderModeNotEdit()
    {
        // setup
        configureEmptyComponent( RenderMode.LIVE );

        // exercise
        portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response = "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }
}