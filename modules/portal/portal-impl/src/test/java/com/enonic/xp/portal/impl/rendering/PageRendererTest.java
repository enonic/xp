package com.enonic.xp.portal.impl.rendering;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.postprocess.TestPostProcessInjection;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageRendererTest
{
    private PortalRequest portalRequest;

    private Content content;

    private PageRenderer renderer;

    ControllerScriptFactory controllerScriptFactory;

    ProcessorChainResolver processorChainResolver;

    PortalScriptService portalScriptService;

    @BeforeEach
    public void before()
    {

        final PostProcessorImpl postProcessor = new PostProcessorImpl();
        postProcessor.addInjection( new TestPostProcessInjection() );
        controllerScriptFactory = mock( ControllerScriptFactory.class );
        processorChainResolver = mock( ProcessorChainResolver.class );
        portalScriptService = mock( PortalScriptService.class );

        renderer = new PageRenderer( postProcessor, portalScriptService, processorChainResolver, controllerScriptFactory );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
        this.portalRequest.setMode( RenderMode.EDIT );

        when( processorChainResolver.resolve( this.portalRequest ) ).thenReturn( ResponseProcessorDescriptors.empty() );
    }

    @Test
    public void descriptor_without_controller()
    {
        // setup
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );
        this.portalRequest.setPageDescriptor( PageDescriptor.create()
                                                  .key( DescriptorKey.from( "myapplication:page" ) )
                                                  .config( Form.create().build() )
                                                  .regions( RegionDescriptors.create().build() )
                                                  .build() );

        when( portalScriptService.hasScript( ResourceKey.from( "myapplication:/site/pages/page/page.js" ) ) ).thenReturn( false );
        // exercise
        PortalResponse portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response =
            "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body data-portal-component-type=\"page\"></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    @Test
    public void descriptor_with_controller()
    {
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );

        final PageDescriptor descriptor = PageDescriptor.create()
            .key( DescriptorKey.from( "myapplication:page" ) )
            .config( Form.create().build() )
            .regions( RegionDescriptors.create().build() )
            .build();

        this.portalRequest.setPageDescriptor( descriptor );

        final ResourceKey expectedResourceKey = ResourceKey.from( "myapplication:/site/pages/page/page.js" );
        when( portalScriptService.hasScript( expectedResourceKey ) ).thenReturn( true );
        final ControllerScript controllerScript = mock( ControllerScript.class );
        when( controllerScriptFactory.fromScript( expectedResourceKey ) ).thenReturn( controllerScript );
        PortalResponse portalResponse = PortalResponse.create().build();
        when( controllerScript.execute( this.portalRequest ) ).thenReturn( portalResponse );
        PortalResponse result = renderer.render( content, portalRequest );

        assertSame( portalResponse, result );
    }

    @Test
    public void contentWithRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );

        // exercise
        PortalResponse portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response =
            "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body data-portal-component-type=\"page\"></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    @Test
    public void contentWithRenderModeNotEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.LIVE );
        content = createContent( "aaa", "my_content", "myapplication:my_type" );
        this.portalRequest.setContent( this.content );

        // exercise
        PortalResponse portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response = "<html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    @Test
    public void defaultFragmentRendering()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        content = createFragmentContent( "aaa", "my_content" );
        this.portalRequest.setContent( this.content );

        // exercise
        PortalResponse portalResponse = renderer.render( content, portalRequest );

        // verify
        final String response =
            "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/><title>My Content</title></head><body data-portal-component-type=\"page\"><!--#COMPONENT fragment--></body></html>";
        assertEquals( response, portalResponse.getAsString() );
    }

    private Content createContent( final String id, final String name, final String contentTypeName )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create()
            .id( ContentId.from( id ) )
            .parentPath( ContentPath.ROOT )
            .name( name )
            .valid( true )
            .createdTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .language( Locale.ENGLISH )
            .displayName( "My Content" )
            .modifiedTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.from( contentTypeName ) )
            .addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) )
            .build();
    }

    private Content createFragmentContent( final String id, final String name )
    {
        final PropertyTree metadata = new PropertyTree();
        metadata.setLong( "myProperty", 1L );

        return Content.create()
            .id( ContentId.from( id ) )
            .parentPath( ContentPath.ROOT )
            .name( name )
            .valid( true )
            .createdTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .owner( PrincipalKey.from( "user:myStore:me" ) )
            .language( Locale.ENGLISH )
            .displayName( "My Content" )
            .modifiedTime( Instant.parse( "2013-08-23T12:55:09.162Z" ) )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .type( ContentTypeName.fragment() )
            .addExtraData( new ExtraData( XDataName.from( "myApplication:myField" ), metadata ) )
            .build();
    }
}
