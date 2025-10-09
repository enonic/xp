package com.enonic.xp.core.impl.content.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.core.impl.content.ContentConfig;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.SiteConfigs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HtmlAreaContentProcessorTest
{
    private HtmlAreaContentProcessor htmlAreaContentProcessor;

    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private CmsService cmsService;

    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ContentTypeName contentTypeName;

    @BeforeEach
    public void setUp()
    {

        this.cmsService = Mockito.mock( CmsService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        contentTypeName = ContentTypeName.from( "myContentType" );

        final ContentType contentType = ContentType.create()
            .name( contentTypeName )
            .superType( ContentTypeName.folder() )
            .form( Form.create()
                       .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
                       .build() )
            .build();

        when( contentTypeService.getByName( any() ) ).thenReturn( contentType );

        ContentConfig contentConfig = Mockito.mock( ContentConfig.class, invocation -> invocation.getMethod().getDefaultValue() );

        htmlAreaContentProcessor = new HtmlAreaContentProcessor( contentConfig );
        htmlAreaContentProcessor.setContentTypeService( contentTypeService );
        htmlAreaContentProcessor.setCmsService( cmsService );
        htmlAreaContentProcessor.setXDataService( xDataService );
        htmlAreaContentProcessor.setPageDescriptorService( pageDescriptorService );
        htmlAreaContentProcessor.setPartDescriptorService( partDescriptorService );
        htmlAreaContentProcessor.setLayoutDescriptorService( layoutDescriptorService );
    }

    @Test
    public void empty_data()
    {
        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Content.create().name( "name" ).type( contentTypeName ).parentPath( ContentPath.ROOT ).build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat(result.getContent().getProcessedReferences() ).isEmpty();
    }

    @Test
    public void content_data()
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );
        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Content.create().name( "name" ).type( contentTypeName ).parentPath( ContentPath.ROOT ).data( data ).build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void content_deep_data()
    {

        final ContentTypeName deepTypeName = ContentTypeName.from( "deepContentType" );

        final ContentType deepContentType = ContentType.create()
            .name( deepTypeName )
            .superType( ContentTypeName.folder() )
            .form( Form.create()
                       .addFormItem( FormOptionSet.create()
                                         .name( "set" )
                                         .addOptionSetOption( FormOptionSetOption.create()
                                                                  .name( "option" )
                                                                  .addFormItem( Input.create()
                                                                                    .name( "htmlData" )
                                                                                    .label( "htmlData" )
                                                                                    .inputType( InputTypeName.HTML_AREA )
                                                                                    .build() )
                                                                  .build() )
                                         .build() )
                       .build() )
            .build();

        reset( contentTypeService );
        when( contentTypeService.getByName( any() ) ).thenReturn( deepContentType );

        final PropertyTree data = new PropertyTree();
        PropertySet set1 = data.addSet( "set" );
        PropertySet set2 = data.addSet( "set" );

        PropertySet option1 = set1.addSet( "option" );
        PropertySet option2 = set1.addSet( "option" );
        PropertySet option3 = set2.addSet( "option" );

        option1.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id1\" src=\"image/123\"/>" ) );

        option2.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans02.jpg\" data-src=\"image://image-id2\" src=\"image/123\"/>" ) );
        option3.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans03.jpg\" data-src=\"image://image-id3\" src=\"image/123\"/>" ) );
        option3.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans04.jpg\" data-src=\"image://image-id4\" src=\"image/123\"/>" ) );

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Content.create().name( "name" ).type( deepTypeName ).parentPath( ContentPath.ROOT ).data( data ).build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() )
            .containsExactly( ContentId.from( "image-id1" ), ContentId.from( "image-id2" ), ContentId.from( "image-id3" ),
                              ContentId.from( "image-id4" ) );
    }

    @Test
    public void site_config_data()
    {
        when( cmsService.getDescriptor( ApplicationKey.SYSTEM ) ).thenReturn( CmsDescriptor.create()
                                                                                  .applicationKey( ApplicationKey.SYSTEM )
                                                                                  .form( Form.create()
                                                                                             .addFormItem( Input.create()
                                                                                                               .name( "htmlData" )
                                                                                                               .label( "htmlData" )
                                                                                                               .inputType(
                                                                                                                   InputTypeName.HTML_AREA )
                                                                                                               .build() )
                                                                                             .build() )
                                                                                  .build() );

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig = SiteConfig.create().config( data ).application( ApplicationKey.SYSTEM ).build();
        PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Site.create()
                                .name( "myContentName" )
                                .type( ContentTypeName.site() ).parentPath( ContentPath.ROOT ).data( siteData )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void extra_data()
    {
        final XDataName xDataName = XDataName.from( "xDataName" );

        final XData xData = XData.create()
            .name( xDataName )
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Site.create()
                                .name( "myContentName" )
                                .type( ContentTypeName.site() )
                                .parentPath( ContentPath.ROOT )
                                .data( new PropertyTree() )
                                .extraDatas( ExtraDatas.create()
                                                 .add(
                                                     new ExtraData( XDataName.from( "xDataName" ), data ) )
                                                 .build() )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void page_config_data()
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img src =\"source\" data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .config( form )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "aaa:bbb" ) )
            .build();
        when( pageDescriptorService.getByKey( Mockito.isA( DescriptorKey.class ) ) ).thenReturn( pageDescriptor );

        final Page page = Page.create().config( data ).descriptor( pageDescriptor.getKey() ).build();

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Media.create()
                                .name( "myContentName" )
                                .type( contentTypeName )
                                .page( page )
                                .parentPath( ContentPath.ROOT )
                                .data( new PropertyTree() )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void component_config_data()
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id\" src=\"image/123\"/>" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
        when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final PartComponent partComponent =
            PartComponent.create().descriptor( "myapp:part" ).descriptor( partDescriptor.getKey() ).config( data ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() )
            .key( DescriptorKey.from( "app:page" ) )
            .config( Form.empty() )
            .build();
        when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create()
            .config( new PropertyTree() )
            .descriptor( pageDescriptor.getKey() )
            .regions( Regions.create().add( Region.create().name( "region" ).add( partComponent ).build() ).build() )
            .build();

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Media.create()
                                .name( "myContentName" )
                                .type( contentTypeName )
                                .page( page )
                                .parentPath( ContentPath.ROOT )
                                .data( new PropertyTree() )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void inner_component_data()
    {
        final PropertyTree data1 = new PropertyTree();
        data1.addProperty( "htmlData",
                           ValueFactory.newString( "<img data-src=\"image://image-id1\" src=\"image/123\" src=\"image/123\"/>" ) );

        final PropertyTree data2 = new PropertyTree();
        data2.addProperty( "htmlData",
                           ValueFactory.newString( "<img data-src=\"image://image-id2\" src=\"image/123\" src=\"image/123\"/>" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
        when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final PartComponent partComponent =
            PartComponent.create().descriptor( "myapp:partest" ).descriptor( partDescriptor.getKey() ).config( data2 ).build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
            .key( DescriptorKey.from( "app:layout" ) )
            .config( form )
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "part" ).build() ).build() )
            .build();
        when( layoutDescriptorService.getByKey( layoutDescriptor.getKey() ) ).thenReturn( layoutDescriptor );

        final LayoutComponent layoutComponent = LayoutComponent.create()
            .descriptor( "myapp:layout" )
            .descriptor( layoutDescriptor.getKey() )
            .regions( Regions.create().add( Region.create().name( "part" ).add( partComponent ).build() ).build() )
            .config( data1 )
            .build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() )
            .key( DescriptorKey.from( "app:page" ) )
            .config( Form.empty() )
            .build();
        when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create()
            .config( new PropertyTree() )
            .descriptor( pageDescriptor.getKey() )
            .regions( Regions.create().add( Region.create().name( "region" ).add( layoutComponent ).build() ).build() )
            .build();

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Media.create()
                                .name( "myContentName" )
                                .type( contentTypeName )
                                .page( page )
                                .parentPath( ContentPath.ROOT )
                                .data( new PropertyTree() )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id1"), ContentId.from("image-id2" ) );
    }

    @Test
    public void text_component_value()
    {
        final TextComponent textComponent = TextComponent.create().text( "<img data-src=\"image://image-id\" src=\"image/123\"/>" ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() )
            .key( DescriptorKey.from( "app:page" ) )
            .config( Form.empty() )
            .build();

        when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create()
            .config( new PropertyTree() )
            .descriptor( pageDescriptor.getKey() ).regions( Regions.create()
                          .add( Region.create()
                                    .name( "region" )
                                    .add( PartComponent.create()
                                              .descriptor( "myapp:part" )
                                              .descriptor( DescriptorKey.from( "app:my" ) )
                                              .config( new PropertyTree() )
                                              .build() )
                                    .add( textComponent )
                                    .build() )
                          .build() )
            .build();

        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( Media.create()
                                .name( "myContentName" )
                                .type( contentTypeName )
                                .page( page )
                                .parentPath( ContentPath.ROOT )
                                .data( new PropertyTree() )
                                .build() )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
    }

    @Test
    public void text_component_sanitized()
    {
        final TextComponent textComponent = TextComponent.create().text( "<img data-src=\"image://image-id\"/>" ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() )
            .key( DescriptorKey.from( "app:page" ) )
            .config( Form.empty() )
            .build();

        when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create()
            .config( new PropertyTree() )
            .descriptor( pageDescriptor.getKey() ).regions( Regions.create()
                          .add( Region.create()
                                    .name( "region" )
                                    .add( PartComponent.create()
                                              .descriptor( "myapp:part" )
                                              .descriptor( DescriptorKey.from( "app:my" ) )
                                              .config( new PropertyTree() )
                                              .build() )
                                    .add( textComponent )
                                    .build() )
                          .build() )
            .build();

        final Media content = Media.create()
            .name( "myContentName" )
            .type( contentTypeName )
            .page( page )
            .parentPath( ContentPath.ROOT )
            .data( new PropertyTree() )
            .build();
        final ProcessUpdateParams params = ProcessUpdateParams.create()
            .content( content )
            .build();

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).containsExactly( ContentId.from( "image-id" ) );
        assertEquals( "<img data-src=\"image://image-id\"/>",
                      ( (TextComponent) result.getContent().getPage().getComponent( ComponentPath.from( "/region/1" ) ) ).getText() );
    }

    @Test
    public void component_config_sanitized_enabled()
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id\" />" ) );

        final Form form = Form.create()
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
        when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final PartComponent partComponent =
            PartComponent.create().descriptor( "myapp:part" ).descriptor( partDescriptor.getKey() ).config( data ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create()
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() )
            .key( DescriptorKey.from( "app:page" ) )
            .config( Form.empty() )
            .build();
        when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create()
            .config( new PropertyTree() )
            .descriptor( pageDescriptor.getKey() )
            .regions( Regions.create().add( Region.create().name( "region" ).add( partComponent ).build() ).build() )
            .build();

        final Media content = Media.create()
            .name( "myContentName" )
            .type( contentTypeName )
            .page( page )
            .parentPath( ContentPath.ROOT )
            .data( new PropertyTree() )
            .build();

        final ProcessUpdateParams params = ProcessUpdateParams.create().content( content ).build();

        final ContentConfig contentConfig = Mockito.mock( ContentConfig.class, invocation -> true );

        htmlAreaContentProcessor = new HtmlAreaContentProcessor( contentConfig );
        htmlAreaContentProcessor.setContentTypeService( contentTypeService );
        htmlAreaContentProcessor.setCmsService( cmsService );
        htmlAreaContentProcessor.setXDataService( xDataService );
        htmlAreaContentProcessor.setPageDescriptorService( pageDescriptorService );
        htmlAreaContentProcessor.setPartDescriptorService( partDescriptorService );
        htmlAreaContentProcessor.setLayoutDescriptorService( layoutDescriptorService );

        final ProcessUpdateResult result = htmlAreaContentProcessor.processUpdate( params );

        assertThat( result.getContent().getProcessedReferences() ).isEmpty();
    }

    @Test
    public void supports()
    {
        assertTrue( htmlAreaContentProcessor.supports( contentTypeName  ) );
    }

    @Test
    public void create()
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id1\" src=\"/admin/rest-v2/cs/cms/features/content/content/image/5a5fc786-a4e6-4a4d-a21a-19ac6fd4784b?ts=1438862613943&amp;size=679&amp;scaleWidth=true\"/>" ) );

        final XDataName xDataName = XDataName.from( "xDataName" );

        final XData xData = XData.create()
            .name( xDataName )
            .addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() )
            .build();

        final PropertyTree extraData = new PropertyTree();
        extraData.addProperty( "htmlData", ValueFactory.newString(
            "<img alt=\"Dictyophorus_spumans02.jpg\" data-src=\"image://image-id2\" src=\"/admin/rest-v2/cs/cms/features/5a5fc786-a4e6-4a4d-a21a-19ac6fd4784b\"/>" ) );

        when( xDataService.getByName( xDataName ) ).thenReturn( xData );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .parent( ContentPath.ROOT )
            .contentData( data )
            .extraDatas( ExtraDatas.create().add( new ExtraData( XDataName.from( "xDataName" ), extraData ) ).build() )
            .type( contentTypeName )
            .build();
        final ProcessCreateParams processCreateParams = new ProcessCreateParams( createContentParams, null, ContentIds.empty() );
        final ProcessCreateResult result = htmlAreaContentProcessor.processCreate( processCreateParams );

        var captor = ArgumentCaptor.forClass( GetContentTypeParams.class );

        verify( contentTypeService ).getByName( captor.capture() );
        assertEquals( contentTypeName, captor.getValue().getContentTypeName() );
        assertThat( result.getProcessedReferences() ).containsExactly( ContentId.from( "image-id1"), ContentId.from("image-id2" ) );
    }
}
