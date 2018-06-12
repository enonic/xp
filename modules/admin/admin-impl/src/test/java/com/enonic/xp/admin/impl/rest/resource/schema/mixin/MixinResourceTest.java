package com.enonic.xp.admin.impl.rest.resource.schema.mixin;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

public class MixinResourceTest
    extends AdminResourceTestSupport
{
    private final static MixinName MY_MIXIN_QUALIFIED_NAME_1 = MixinName.from( "myapplication:input_text_1" );

    private final static XDataName MY_XDATA_QUALIFIED_NAME_1 = XDataName.from( "myapplication:input_text_1" );

    private final static String MY_MIXIN_INPUT_NAME_1 = "input_text_1";

    private final static MixinName MY_MIXIN_QUALIFIED_NAME_2 = MixinName.from( "myapplication:text_area_2" );

    private final static XDataName MY_XDATA_QUALIFIED_NAME_2 = XDataName.from( "myapplication:text_area_2" );

    private final static String MY_MIXIN_INPUT_NAME_2 = "text_area_2";

    private MixinService mixinService;

    private XDataService xDataService;

    private LocaleService localeService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    private MixinResource resource;

    @Override
    protected Object getResourceInstance()
    {
        mixinService = Mockito.mock( MixinService.class );
        xDataService = Mockito.mock( XDataService.class );
        localeService = Mockito.mock( LocaleService.class );
        contentService = Mockito.mock( ContentService.class );
        siteService = Mockito.mock( SiteService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );

        resource = new MixinResource();
        resource.setMixinService( mixinService );
        resource.setXDataService( xDataService );
        resource.setLocaleService( localeService );
        resource.setContentService( contentService );
        resource.setSiteService( siteService );
        resource.setContentTypeService( contentTypeService );

        return resource;
    }

    @Test
    public final void test_get_mixin()
        throws Exception
    {
        Mixin mixin = Mixin.create().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        String response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get().getAsString();

        assertJson( "get_mixin.json", response );
    }

    @Test
    public final void test_get_mixin_i18n()
        throws Exception
    {
        Mixin mixin = Mixin.create().
            createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).
            displayNameI18nKey( "key.display-name" ).
            descriptionI18nKey( "key.description" ).
            name( MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).labelI18nKey( "key.label" ).helpText( "Help text line 1" ).helpTextI18nKey( "key.help-text" ).required(
                true ).build() ).build();

        Mockito.when( mixinService.getByName( Mockito.isA( MixinName.class ) ) ).thenReturn( mixin );

        final MessageBundle messageBundle = Mockito.mock( MessageBundle.class );
        Mockito.when( messageBundle.localize( "key.label" ) ).thenReturn( "translated.label" );
        Mockito.when( messageBundle.localize( "key.help-text" ) ).thenReturn( "translated.helpText" );
        Mockito.when( messageBundle.localize( "key.display-name" ) ).thenReturn( "translated.displayName" );
        Mockito.when( messageBundle.localize( "key.description" ) ).thenReturn( "translated.description" );

        Mockito.when( this.localeService.getBundle( any(), any() ) ).thenReturn( messageBundle );

        String response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get().getAsString();

        assertJson( "get_mixin_i18n.json", response );
    }

    @Test
    public final void test_get_mixin_not_found()
        throws Exception
    {
        Mockito.when( mixinService.getByName( any( MixinName.class ) ) ).thenReturn( null );

        final MockRestResponse response = request().path( "schema/mixin" ).queryParam( "name", MY_MIXIN_QUALIFIED_NAME_1.toString() ).get();
        Assert.assertEquals( 404, response.getStatus() );
    }

    @Test
    public final void test_list_mixins()
        throws Exception
    {
        Mixin mixin1 = Mixin.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_1.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).build();

        Mixin mixin2 = Mixin.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_MIXIN_QUALIFIED_NAME_2.toString() ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_2 ).inputType( InputTypeName.TEXT_AREA ).label( "Text Area" ).required(
                true ).helpText( "Help text area" ).required( true ).build() ).build();

        Mockito.when( mixinService.getAll() ).thenReturn( Mixins.from( mixin1, mixin2 ) );

        String result = request().path( "schema/mixin/list" ).get().getAsString();

        assertJson( "list_mixins.json", result );
    }

    @Test
    public void getApplicationXDataForContentType()
        throws Exception
    {

        final ContentTypeName contentTypeName = ContentTypeName.from( "app:testContentType" );

        final XData xdata1 = XData.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_XDATA_QUALIFIED_NAME_1 ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).allowContentType( contentTypeName.toString() ).build();

        final XData xdata2 = XData.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_XDATA_QUALIFIED_NAME_2 ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_2 ).inputType( InputTypeName.TEXT_AREA ).label( "Text Area" ).required(
                true ).helpText( "Help text area" ).required( true ).build() ).build();

        final XData xdata3 = XData.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            XDataName.from( "myapplication:text_area_3" ) ).addFormItem(
            Input.create().name( "input_name_3" ).inputType( InputTypeName.TEXT_AREA ).label( "Text Area" ).required( true ).helpText(
                "Help text area" ).required( true ).build() ).allowContentType( "app:anotherContentType" ).build();

        final SiteDescriptor siteDescriptor =
            SiteDescriptor.create().metaSteps( MixinNames.from( xdata1.getName().toString(), xdata3.getName().toString() ) ).build();
        Mockito.when( siteService.getDescriptor( contentTypeName.getApplicationKey() ) ).thenReturn( siteDescriptor );

        Mockito.when( mixinService.getByNames( any() ) ).thenReturn( Mixins.empty() );
        Mockito.when( xDataService.getByNames( XDataNames.from( xdata1.getName().toString(), xdata3.getName().toString() ) ) ).thenReturn(
            XDatas.from( xdata1 ) );
        Mockito.when( xDataService.getByNames( XDataNames.from( xdata2.getName().toString(), xdata3.getName().toString() ) ) ).thenReturn(
            XDatas.from( xdata2 ) );

        Mockito.when( xDataService.getByApplication( any() ) ).thenReturn( XDatas.from( xdata2, xdata3 ) );

        String result = request().path( "schema/mixin/getApplicationXDataForContentType" ).
            queryParam( "contentTypeName", contentTypeName.toString() ).
            queryParam( "applicationKey", contentTypeName.getApplicationKey().toString() ).
            get().
            getAsString();

        assertJson( "get_content_x_data_for_content_type.json", result );

    }

    @Test
    public void getContentXData()
        throws Exception
    {
        final XData xdata1 = XData.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_XDATA_QUALIFIED_NAME_1 ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_1 ).inputType( InputTypeName.TEXT_LINE ).label( "Line Text 1" ).required(
                true ).helpText( "Help text line 1" ).required( true ).build() ).allowContentType( "^app:*" ).build();

        final XData xdata2 = XData.create().createdTime( LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC ) ).name(
            MY_XDATA_QUALIFIED_NAME_2 ).addFormItem(
            Input.create().name( MY_MIXIN_INPUT_NAME_2 ).inputType( InputTypeName.TEXT_AREA ).label( "Text Area" ).required(
                true ).helpText( "Help text area" ).required( true ).build() ).allowContentType( "app:testContentType" ).build();

        final ContentType contentType = ContentType.create().name( "app:testContentType" ).superType( ContentTypeName.folder() ).metadata(
            MixinNames.from( xdata1.getName().toString() ) ).build();
        Mockito.when( contentTypeService.getByName( GetContentTypeParams.from( contentType.getName() ) ) ).thenReturn( contentType );
        Mockito.when( contentTypeService.getAll( any() ) ).thenReturn( ContentTypes.from( contentType ) );

        final Content content = Mockito.mock( Content.class );
        Mockito.when( content.getType() ).thenReturn( contentType.getName() );
        Mockito.when( content.getId() ).thenReturn( ContentId.from( "contentId" ) );

        final SiteConfig siteConfig =
            SiteConfig.create().config( new PropertyTree() ).application( contentType.getName().getApplicationKey() ).build();

        final Site site = Site.create().name( "site" ).parentPath( ContentPath.ROOT ).addSiteConfig( siteConfig ).build();

        final SiteDescriptor siteDescriptor = SiteDescriptor.create().metaSteps( MixinNames.from( xdata2.getName().toString() ) ).build();
        Mockito.when( siteService.getDescriptor( contentType.getName().getApplicationKey() ) ).thenReturn( siteDescriptor );

        Mockito.when( contentService.getById( ContentId.from( "contentId" ) ) ).thenReturn( content );
        Mockito.when( contentService.getNearestSite( ContentId.from( "contentId" ) ) ).thenReturn( site );

        Mockito.when( mixinService.getByNames( any() ) ).thenReturn( Mixins.empty() );
        Mockito.when( xDataService.getByNames( XDataNames.from( xdata1.getName() ) ) ).thenReturn( XDatas.from( xdata1 ) );
        Mockito.when( xDataService.getByNames( XDataNames.from( xdata2.getName() ) ) ).thenReturn( XDatas.from( xdata2 ) );

        Mockito.when( xDataService.getByApplication( any() ) ).thenReturn( XDatas.from( xdata2 ) );

        String result = request().path( "schema/mixin/getContentXData" ).queryParam( "contentId", "contentId" ).get().getAsString();

        assertJson( "get_content_x_data.json", result );
    }


    @Test
    public void testMixinIcon()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "mixinicon.png" ) );
        final Icon icon = Icon.from( data, "image/png", Instant.now() );

        Mixin mixin = Mixin.create().
            name( "myapplication:postal_code" ).
            displayName( "My content type" ).
            icon( icon ).
            addFormItem( Input.create().name( "postal_code" ).label( "Postal code" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        // exercise
        final Response response = this.resource.getIcon( "myapplication:postal_code", 20, null );
        final BufferedImage mixinIcon = (BufferedImage) response.getEntity();

        // verify
        assertImage( mixinIcon, 20 );
    }

    @Test
    public void testMixinIcon_default_image()
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( "mixin.svg" );
        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        org.junit.Assert.assertArrayEquals( ByteStreams.toByteArray( in ), (byte[]) response.getEntity() );
    }

    @Test
    public void getIconIsSvg()
        throws Exception
    {
        byte[] data = Resources.toByteArray( getClass().getResource( "icon-black.svg" ) );
        final Icon icon = Icon.from( data, "image/svg+xml", Instant.now() );

        Mixin mixin = Mixin.create().
            name( "myapplication:icon_svg_test" ).
            displayName( "My content type" ).
            icon( icon ).
            addFormItem( Input.create().name( "icon_svg_test" ).label( "SVG icon test" ).inputType( InputTypeName.TEXT_LINE ).build() ).
            build();
        setupMixin( mixin );

        final Response response = this.resource.getIcon( "myapplication:icon_svg_test", 20, null );

        assertNotNull( response.getEntity() );
        assertEquals( icon.getMimeType(), response.getMediaType().toString() );
        org.junit.Assert.assertArrayEquals( data, (byte[]) response.getEntity() );
    }

    private void setupMixin( final Mixin mixin )
    {
        Mockito.when( mixinService.getByName( mixin.getName() ) ).thenReturn( mixin );
    }

    private void assertImage( final BufferedImage image, final int size )
    {
        assertNotNull( image );
        assertEquals( size, image.getWidth() );
    }

}
