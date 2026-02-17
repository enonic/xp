package com.enonic.xp.portal.impl.processor;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.util.Version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseProcessorExecutorTest
{
    PortalRequest portalRequest;

    PortalResponse portalResponse;

    ResourceService resourceService;

    PortalScriptService scriptService;

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalResponse = PortalResponse.create().build();

        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapplication" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        this.resourceService = mock( ResourceService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( resourceService, null, application );

        final PortalScriptServiceImpl portalScriptService = new PortalScriptServiceImpl( runtimeFactory );
        portalScriptService.initialize();
        scriptService = portalScriptService;
    }

    @Test
    void testExecuteResponseProcessor()
    {
        this.portalResponse = PortalResponse.create()
            .body( ByteSource.wrap( "DATA".getBytes( StandardCharsets.UTF_8 ) ) )
            .contentType( MediaType.XHTML_UTF_8 )
            .build();

        final PortalResponse response = execute( "myapplication:/processor/processor.js" );
        assertThat( response.getBody() ).isSameAs( portalResponse.getBody() );
        assertThat( response.getContentType() ).isSameAs( portalResponse.getContentType() );
        assertThat( response.getContributions( HtmlTag.BODY_END ) ).containsExactly(
            "<script src=\"http://some.cdn/js/tracker.js\"></script>" );
    }

    @Test
    void testExecuteResponseProcessorNotImplementingMethod()
    {
        var e = assertThrows( RenderException.class, () -> execute( "myapplication:/processor/missing-processor.js" ) );
        assertEquals(
            "Missing exported function [responseProcessor] in response filter [myapplication:/cms/processors/missing-processor.js]",
            e.getMessage() );
    }

    private PortalResponse execute( final String scriptKey )
    {
        final ResourceKey resourceKey = ResourceKey.from( scriptKey );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final URL resourceUrl =
                ResponseProcessorExecutorTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );
        return new ResponseProcessorExecutor( scriptService ).execute( ResponseProcessorDescriptor.create()
                                                                           .application( resourceKey.getApplicationKey() )
                                                                           .name( getNameWithoutExtension( resourceKey.getName() ) )
                                                                           .build(), this.portalRequest, this.portalResponse );
    }


    private static String getNameWithoutExtension( final String name )
    {
        final int pos = name.lastIndexOf( '.' );
        return pos > 0 ? name.substring( 0, pos ) : name;
    }
}
