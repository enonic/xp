package com.enonic.xp.portal.impl.rendering;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.postprocess.TestPostProcessInjection;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private Content content;

    private PageRenderer renderer;

    @BeforeEach
    public void before()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
        this.portalResponse = PortalResponse.create().build();
        this.portalRequest.setMode( RenderMode.EDIT );
    }

    @Test
    public void contentWithRenderModeEdit()
    {
        // setup
        configureEmptyComponent( RenderMode.EDIT );
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );

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
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );

        // exercise
        portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response = "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    @Test
    public void defaultFragmentRendering()
    {
        // setup
        configureEmptyComponent( RenderMode.EDIT );
        content = createFragmentContent( "aaa", "my_content" );
        this.portalRequest.setContent( this.content );

        // exercise
        portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response =
            "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body data-portal-component-type=\"page\"><!--#COMPONENT fragment--></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    private void configureEmptyComponent( RenderMode mode )
    {
        // setup
        portalRequest.setMode( mode );
        renderer = new PageRenderer();

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInjection( new TestPostProcessInjection() );
        renderer.setPostProcessor( postProcessor );

        final ProcessorChainResolver resolver = Mockito.mock( ProcessorChainResolver.class );
        Mockito.when( resolver.resolve( this.portalRequest ) ).thenReturn( ResponseProcessorDescriptors.empty() );
        renderer.setProcessorChainResolver( resolver );
    }

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
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            build();
    }

    private Content createFragmentContent( final String id, final String name )
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
            type( ContentTypeName.fragment() ).
            addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) ).
            build();
    }
}
