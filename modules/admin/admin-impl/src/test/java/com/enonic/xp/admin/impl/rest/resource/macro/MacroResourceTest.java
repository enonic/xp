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
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorFactory;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class MacroResourceTest
    extends AdminResourceTestSupport
{

    private MacroDescriptorService macroDescriptorService;

    private MacroProcessorFactory macroProcessorFactory;

    private PortalUrlService portalUrlService;

    private ContentService contentService;

    private LocaleService localeService;

    private MacroResource macroResource;

    @Override
    protected Object getResourceInstance()
    {
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        this.macroProcessorFactory = Mockito.mock( MacroProcessorFactory.class );
        this.portalUrlService = Mockito.mock( PortalUrlService.class );
        this.contentService = Mockito.mock( ContentService.class );
        this.localeService = Mockito.mock( LocaleService.class );

        this.macroResource = new MacroResource();
        macroResource.setMacroDescriptorService( this.macroDescriptorService );
        macroResource.setMacroProcessorFactory( this.macroProcessorFactory );
        macroResource.setPortalUrlService( this.portalUrlService );
        macroResource.setContentService( this.contentService );
        macroResource.setLocaleService( this.localeService );

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
        final MacroDescriptor macroDescriptor1 = newMacroDescriptor( "my-app1:macro1", "A macro" );
        final MacroDescriptor macroDescriptor2 = newMacroDescriptor( "my-app2:macro2", "B macro" );
        final MacroDescriptor macroDescriptor3 = newMacroDescriptor( "my-app3:macro3", "C macro" );

        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.SYSTEM ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor1 ) );
        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.from( "appKey1" ) ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor2 ) );
        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.from( "appKey2" ) ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor3 ) );

        String response = request().
            path( "macro/getByApps" ).
            entity( "{\"appKeys\": [\"appKey1\", \"appKey2\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        assertJson( "get_macros.json", response );
    }

    @Test
    public void testGetByApps_i18n()
        throws Exception
    {
        final Form descriptorForm = Form.create().
            addFormItem( Input.create().
                name( "columns" ).
                maximizeUIInputWidth( true ).
                label( "Columns" ).
                labelI18nKey( "key.label" ).
                helpTextI18nKey( "key.help-text" ).
                inputType( InputTypeName.DOUBLE ).
                build() ).
            build();

        final MacroDescriptor macroDescriptor1 = newMacroDescriptor( "my-app1:macro1", "A macro", "key.a.display-name", descriptorForm );
        final MacroDescriptor macroDescriptor2 = newMacroDescriptor( "my-app2:macro2", "B macro", "key.b.display-name", "key.description" );
        final MacroDescriptor macroDescriptor3 = newMacroDescriptor( "my-app3:macro3", "C macro", "key.c.display-name" );

        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.SYSTEM ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor1 ) );
        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.from( "appKey1" ) ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor2 ) );
        Mockito.when( this.macroDescriptorService.getByApplication( ApplicationKey.from( "appKey2" ) ) ).thenReturn(
            MacroDescriptors.from( macroDescriptor3 ) );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );
        Mockito.when( messageBundle.localize( "key.description" ) ).thenReturn( "translated.description" );
        Mockito.when( messageBundle.localize( "key.a.display-name" ) ).thenReturn( "translated.A.displayName" );
        Mockito.when( messageBundle.localize( "key.b.display-name" ) ).thenReturn( "translated.B.displayName" );
        Mockito.when( messageBundle.localize( "key.c.display-name" ) ).thenReturn( "translated.C.displayName" );

        Mockito.when( this.localeService.getBundle( Mockito.any(), Mockito.any() ) ).thenReturn( messageBundle );

        String response = request().
            path( "macro/getByApps" ).
            entity( "{\"appKeys\": [\"appKey1\", \"appKey2\"]}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();
        assertJson( "get_macros_i18n.json", response );
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
        Mockito.when( this.macroProcessorFactory.fromScript( any() ) ).thenReturn( macroProcessor );
        Mockito.when( this.portalUrlService.pageUrl( any() ) ).thenReturn( "/portal/preview/draft/mysite/page" );

        final Site site = newSite();
        Mockito.when( this.contentService.getByPath( any() ) ).thenReturn( site );
        Mockito.when( this.contentService.getNearestSite( any() ) ).thenReturn( site );

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

    private MacroDescriptor newMacroDescriptor( final String key, final String name, final String nameI18nKey, final Form config )
    {
        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( key ) ).
            description( "my description" ).
            displayName( name ).
            displayNameI18nKey( nameI18nKey ).
            form( config ).
            build();

        return macroDescriptor;
    }

    private MacroDescriptor newMacroDescriptor( final String key, final String name, final String nameI18nKey,
                                                final String descriptionI18nKey )
    {
        final Form config = Form.create().build();

        final MacroDescriptor macroDescriptor = MacroDescriptor.create().
            key( MacroKey.from( key ) ).
            description( "my description" ).
            descriptionI18nKey( descriptionI18nKey ).
            displayName( name ).
            displayNameI18nKey( nameI18nKey ).
            form( config ).
            build();

        return macroDescriptor;
    }

    private MacroDescriptor newMacroDescriptor( final String key, final String name, final String nameI18nKey )
    {
        final Form config = Form.create().build();

        return this.newMacroDescriptor( key, name, nameI18nKey, config );
    }

    private MacroDescriptor newMacroDescriptor( final String key, final String name )
    {
        return this.newMacroDescriptor( key, name, null );
    }

    public static Site newSite()
    {
        final PropertyTree siteConfigConfig = new PropertyTree();
        siteConfigConfig.setLong( "Field", 42L );
        final SiteConfig siteConfig = SiteConfig.create().
            application( ApplicationKey.from( "myapp" ) ).
            config( siteConfigConfig ).
            build();

        final Site.Builder site = Site.create();
        site.id( ContentId.from( "1004242" ) );
        site.siteConfigs( SiteConfigs.from( siteConfig ) );
        site.name( "my-content" );
        site.parentPath( ContentPath.ROOT );
        return site.build();
    }
}
