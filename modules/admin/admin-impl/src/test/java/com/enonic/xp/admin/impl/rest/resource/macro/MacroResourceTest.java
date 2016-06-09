package com.enonic.xp.admin.impl.rest.resource.macro;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.form.Form;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorScriptFactory;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.url.PortalUrlService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class MacroResourceTest
    extends AdminResourceTestSupport
{

    private MacroDescriptorService macroDescriptorService;

    private MacroProcessorScriptFactory macroProcessorScriptFactory;

    private PortalUrlService portalUrlService;

    private MacroResource macroResource;

    @Override
    protected Object getResourceInstance()
    {
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        this.macroProcessorScriptFactory = Mockito.mock( MacroProcessorScriptFactory.class );
        this.portalUrlService = Mockito.mock( PortalUrlService.class );

        this.macroResource = new MacroResource();
        macroResource.setMacroDescriptorService( this.macroDescriptorService );
        macroResource.setMacroProcessorScriptFactory( this.macroProcessorScriptFactory );
        macroResource.setPortalUrlService( this.portalUrlService );

        return macroResource;
    }

    @Test
    public void testGetDefaultIcon()
        throws Exception
    {
        String response = request().
            path( "macro/icon/key" ).
            get().getAsString();

        assertNotNull( response.getBytes() );
    }

    @Test
    public void testMacroIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "macro1.svg" ) );
        final Icon icon = Icon.from( data, "image/svg+xml", Instant.now() );

        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( "myapp:macro1" ) ).
            description( "my description" ).
            displayName( "my macro1 name" ).
            form( Form.create().build() ).
            icon( icon ).
            build();

        Mockito.when( macroDescriptorService.getByKey( macroDescriptor.getKey() ) ).thenReturn( macroDescriptor );

        final Response response = this.macroResource.getIcon( "myapp:macro1", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( icon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, (byte[]) response.getEntity() );
    }

    @Test
    public void testGetByApps()
        throws Exception
    {
        Mockito.when( this.macroDescriptorService.getByApplications(
            ApplicationKeys.from( ApplicationKey.SYSTEM.toString(), "appKey1", "appKey2" ) ) ).thenReturn( this.getTestDescriptors() );

        String response = request().
            path( "macro/getByApps" ).
            entity( "{\"appKeys\": [\"appKey1\", \"appKey2\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        assertJson( "get_macros.json", response );
    }

    @Test
    public void testPreview()
        throws Exception
    {
        final Form form = Form.create().build();
        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( "test:uppercase" ) ).
            description( "Uppercase macro" ).
            displayName( "Uppercase macro" ).
            form( form ).
            build();

        final MacroProcessor macroProcessor = ( MacroContext macroContext ) -> {
            final String textParams = macroContext.getBody() + "," + macroContext.getParam( "param1" );
            return PortalResponse.create().
                body( textParams.toUpperCase() ).
                contribution( HtmlTag.BODY_END, "<script type='text/javascript' src='some.js'></script>" ).
                build();
        };

        Mockito.when( this.macroDescriptorService.getByKey( MacroKey.from( "test:uppercase" ) ) ).thenReturn( macroDescriptor );
        Mockito.when( this.macroProcessorScriptFactory.fromScript( any() ) ).thenReturn( macroProcessor );
        Mockito.when( this.portalUrlService.pageUrl( any() ) ).thenReturn( "/portal/preview/draft/mysite/page" );

        final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ResteasyProviderFactory.getContextDataMap().put( HttpServletRequest.class, mockRequest );

        String response = request().path( "macro/preview" ).
            entity( readFromFile( "preview_macro_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "preview_macro_result.json", response );
    }


    @Test
    public void testPreviewString()
        throws Exception
    {
        final Form form = Form.create().build();
        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( "test:uppercase" ) ).
            description( "Uppercase macro" ).
            displayName( "Uppercase macro" ).
            form( form ).
            build();

        Mockito.when( this.macroDescriptorService.getByKey( MacroKey.from( "test:uppercase" ) ) ).thenReturn( macroDescriptor );

        String response = request().path( "macro/previewString" ).
            entity( readFromFile( "preview_string_macro_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "preview_string_macro_result.json", response );
    }

    private MacroDescriptors getTestDescriptors()
        throws Exception
    {
        final Form config = Form.create().build();

        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my-app1:macro1" ) ).
            description( "my description" ).
            displayName( "A macro" ).
            form( config ).
            build();

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.create().
            key( MacroKey.from( "my-app2:macro2" ) ).
            description( "my description" ).
            displayName( "B macro" ).
            form( config ).
            build();

        final MacroDescriptor macroDescriptor3 = MacroDescriptor.create().
            key( MacroKey.from( "my-app3:macro3" ) ).
            description( "my description" ).
            displayName( "C macro" ).
            form( config ).
            build();

        return MacroDescriptors.from( macroDescriptor3, macroDescriptor2, macroDescriptor1 );
    }
}
