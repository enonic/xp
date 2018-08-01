package com.enonic.xp.admin.impl.rest.resource.schema.xdata;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypeParams;
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

import static org.mockito.Matchers.any;

public class XDataResourceTest
    extends AdminResourceTestSupport
{

    private final static XDataName MY_XDATA_QUALIFIED_NAME_1 = XDataName.from( "myapplication:input_text_1" );

    private final static String MY_MIXIN_INPUT_NAME_1 = "input_text_1";

    private final static XDataName MY_XDATA_QUALIFIED_NAME_2 = XDataName.from( "myapplication:text_area_2" );

    private final static String MY_MIXIN_INPUT_NAME_2 = "text_area_2";

    private MixinService mixinService;

    private XDataService xDataService;

    private LocaleService localeService;

    private ContentService contentService;

    private SiteService siteService;

    private ContentTypeService contentTypeService;

    @Override
    protected XDataResource getResourceInstance()
    {
        mixinService = Mockito.mock( MixinService.class );
        xDataService = Mockito.mock( XDataService.class );
        localeService = Mockito.mock( LocaleService.class );
        contentService = Mockito.mock( ContentService.class );
        siteService = Mockito.mock( SiteService.class );
        contentTypeService = Mockito.mock( ContentTypeService.class );

        final XDataResource resource = new XDataResource();
        resource.setMixinService( mixinService );
        resource.setXDataService( xDataService );
        resource.setLocaleService( localeService );
        resource.setContentService( contentService );
        resource.setSiteService( siteService );
        resource.setContentTypeService( contentTypeService );

        return resource;
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

        String result = request().path( "schema/xdata/getApplicationXDataForContentType" ).
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

        String result = request().path( "schema/xdata/getContentXData" ).queryParam( "contentId", "contentId" ).get().getAsString();

        assertJson( "get_content_x_data.json", result );
    }

}
