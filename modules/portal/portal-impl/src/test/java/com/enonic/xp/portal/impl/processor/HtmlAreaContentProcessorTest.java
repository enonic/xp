package com.enonic.xp.portal.impl.processor;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlAreaContentProcessorTest
{
    private ProcessUpdateResult result;

    private HtmlAreaContentProcessor htmlAreaContentProcessor;

    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    private ContentTypeName contentTypeName;

    private ContentType contentType;

    @BeforeEach
    public void setUp()
        throws Exception
    {

        this.siteService = Mockito.mock( SiteService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        contentTypeName = ContentTypeName.from( "myContentType" );

        final GetContentTypeParams params = GetContentTypeParams.from( contentTypeName );
        contentType = ContentType.create().
            name( contentTypeName ).
            superType( ContentTypeName.folder() ).
            form( Form.create().addFormItem(
                Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build() ).build();

        final ProcessUpdateParams processUpdateParams = ProcessUpdateParams.create().
            contentType( contentType ).
            build();

        Mockito.when( contentTypeService.getByName( params ) ).thenReturn( contentType );
        Mockito.when( xDataService.getByNames( Mockito.isA( XDataNames.class ) ) ).thenReturn( XDatas.empty() );

        htmlAreaContentProcessor = new HtmlAreaContentProcessor();
        htmlAreaContentProcessor.setContentTypeService( contentTypeService );
        htmlAreaContentProcessor.setSiteService( siteService );
        htmlAreaContentProcessor.setXDataService( xDataService );
        htmlAreaContentProcessor.setPageDescriptorService( pageDescriptorService );
        htmlAreaContentProcessor.setPartDescriptorService( partDescriptorService );
        htmlAreaContentProcessor.setLayoutDescriptorService( layoutDescriptorService );

        result = htmlAreaContentProcessor.processUpdate( processUpdateParams );
    }

    @Test
    public void empty_data()
        throws IOException
    {

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 0, editableContent.processedReferences.build().getSize() );
    }

    @Test
    public void content_data()
        throws IOException
    {

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            parentPath( ContentPath.ROOT ).
            data( data ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 1, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void site_config_data()
        throws IOException
    {

        final ContentType contentType = ContentType.create().
            name( ContentTypeName.site() ).
            superType( ContentTypeName.folder() ).
            form( Form.create().build() ).build();

        Mockito.when( contentTypeService.getByName( GetContentTypeParams.from( contentType.getName() ) ) ).thenReturn( contentType );
        Mockito.when( siteService.getDescriptor( ApplicationKey.SYSTEM ) ).thenReturn( SiteDescriptor.create().form(
            Form.create().addFormItem(
                Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build() ).build() );

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableSite editableSite = new EditableSite( Site.create().
            name( "myContentName" ).
            type( ContentTypeName.site() ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            siteConfigs( SiteConfigs.create().
                add( SiteConfig.
                    create().
                    config( data ).
                    application( ApplicationKey.SYSTEM ).
                    build() ).
                build() ).
            build() );

        result.getEditor().edit( editableSite );

        assertEquals( 1, editableSite.processedReferences.build().getSize() );
        assertTrue( editableSite.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void extra_data()
        throws IOException
    {
        final XDataName xDataName = XDataName.from( "xDataName" );

        final XData xData = XData.create().name( xDataName ).
            addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

        final ContentType contentType = ContentType.create().name( contentTypeName ).superType( ContentTypeName.folder() ).xData(
            XDataNames.from( xDataName ) ).build();
        Mockito.when( contentTypeService.getByName( GetContentTypeParams.from( ContentTypeName.site() ) ) ).thenReturn( contentType );

        Mockito.when( xDataService.getByNames( XDataNames.from( xDataName ) ) ).thenReturn( XDatas.from( xData ) );

        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id\"/>" ) );

        final EditableSite editableSite = new EditableSite( Site.create().
            name( "myContentName" ).
            type( ContentTypeName.site() ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            extraDatas( ExtraDatas.create().
                add( new ExtraData( XDataName.from( "xDataName" ), data ) ).
                build() ).
            build() );

        result.getEditor().edit( editableSite );

        assertEquals( 1, editableSite.processedReferences.build().getSize() );
        assertTrue( editableSite.processedReferences.build().contains( ContentId.from( "image-id" ) ) );
    }

    @Test
    public void page_config_data()
        throws IOException
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id\"/>" ) );

        final Form form = Form.create().addFormItem(
            Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().config( form ).regions( RegionDescriptors.create().build() ).key(
            DescriptorKey.from( "aaa:bbb" ) ).build();
        Mockito.when( pageDescriptorService.getByKey( Mockito.isA( DescriptorKey.class ) ) ).thenReturn( pageDescriptor );

        final Page page = Page.create().config( data ).descriptor( pageDescriptor.getKey() ).build();

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            page( page ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 1, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id" ) ) );

    }

    @Test
    public void component_config_data()
        throws IOException
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id\"/>" ) );

        final Form form = Form.create().addFormItem(
            Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
        Mockito.when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final PartComponent partComponent =
            PartComponent.create().descriptor( "myapp:part" ).descriptor( partDescriptor.getKey() ).config( data ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().regions(
            RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() ).key(
            DescriptorKey.from( "app:page" ) ).config( Form.create().build() ).build();
        Mockito.when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create().config( new PropertyTree() ).descriptor( pageDescriptor.getKey() ).regions(
            PageRegions.create().add( Region.create().name( "region" ).add( partComponent ).build() ).build() ).build();

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            page( page ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 1, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id" ) ) );

    }

    @Test
    public void inner_component_data()
        throws IOException
    {
        final PropertyTree data1 = new PropertyTree();
        data1.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id1\"/>" ) );

        final PropertyTree data2 = new PropertyTree();
        data2.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id2\"/>" ) );

        final Form form = Form.create().addFormItem(
            Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
        Mockito.when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final PartComponent partComponent =
            PartComponent.create().descriptor( "myapp:partest" ).descriptor( partDescriptor.getKey() ).config( data2 ).build();

        final LayoutDescriptor layoutDescriptor =
            LayoutDescriptor.create().key( DescriptorKey.from( "app:layout" ) ).config( form ).regions(
                RegionDescriptors.create().add( RegionDescriptor.create().name( "part" ).build() ).build() ).build();
        Mockito.when( layoutDescriptorService.getByKey( layoutDescriptor.getKey() ) ).thenReturn( layoutDescriptor );

        final LayoutComponent layoutComponent =
            LayoutComponent.create().descriptor( "myapp:layout" ).descriptor( layoutDescriptor.getKey() ).regions(
                LayoutRegions.create().add( Region.create().name( "part" ).add( partComponent ).build() ).build() ).config( data1 ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().regions(
            RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() ).key(
            DescriptorKey.from( "app:page" ) ).config( Form.create().build() ).build();
        Mockito.when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create().config( new PropertyTree() ).descriptor( pageDescriptor.getKey() ).regions(
            PageRegions.create().add( Region.create().name( "region" ).add( layoutComponent ).build() ).build() ).build();

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            page( page ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 2, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id1" ) ) );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id2" ) ) );

    }

    @Test
    public void text_component_value()
        throws IOException
    {
//        final PropertyTree data = new PropertyTree();
//        data.addProperty( "htmlData", ValueFactory.newString( "<img data-src=\"image://image-id\"/>" ) );

//        final Form form = Form.create().addFormItem(
//            Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

//        final PartDescriptor partDescriptor = PartDescriptor.create().key( DescriptorKey.from( "app:part" ) ).config( form ).build();
//        Mockito.when( partDescriptorService.getByKey( partDescriptor.getKey() ) ).thenReturn( partDescriptor );

        final TextComponent textComponent = TextComponent.create().text( "<img data-src=\"image://image-id\"/>" ).build();

        final PageDescriptor pageDescriptor = PageDescriptor.create().regions(
            RegionDescriptors.create().add( RegionDescriptor.create().name( "region" ).build() ).build() ).key(
            DescriptorKey.from( "app:page" ) ).config( Form.create().build() ).build();

        Mockito.when( pageDescriptorService.getByKey( pageDescriptor.getKey() ) ).thenReturn( pageDescriptor );

        final Page page = Page.create().config( new PropertyTree() ).descriptor( pageDescriptor.getKey() ).regions(
            PageRegions.create().add( Region.create().name( "region" ).add( textComponent ).build() ).build() ).build();

        final EditableContent editableContent = new EditableContent( Media.create().
            name( "myContentName" ).
            type( contentTypeName ).
            page( page ).
            parentPath( ContentPath.ROOT ).
            data( new PropertyTree() ).
            build() );

        result.getEditor().edit( editableContent );

        assertEquals( 1, editableContent.processedReferences.build().getSize() );
        assertTrue( editableContent.processedReferences.build().contains( ContentId.from( "image-id" ) ) );

    }

    @Test
    public void supports()
        throws IOException
    {
        assertTrue( htmlAreaContentProcessor.supports( ContentType.create().
            name( contentTypeName ).
            superType( ContentTypeName.folder() ).
            build() ) );
    }

    @Test
    public void create()
        throws IOException
    {
        final PropertyTree data = new PropertyTree();
        data.addProperty( "htmlData",
                          ValueFactory.newString( "<img alt=\"Dictyophorus_spumans01.jpg\" data-src=\"image://image-id1\"/>" ) );

        final XDataName xDataName = XDataName.from( "xDataName" );

        final XData xData = XData.create().name( xDataName ).
            addFormItem( Input.create().name( "htmlData" ).label( "htmlData" ).inputType( InputTypeName.HTML_AREA ).build() ).build();

        final PropertyTree extraData = new PropertyTree();
        extraData.addProperty( "htmlData",
                               ValueFactory.newString( "<img alt=\"Dictyophorus_spumans02.jpg\" data-src=\"image://image-id2\"/>" ) );

        Mockito.when( xDataService.getByNames( XDataNames.from( xDataName ) ) ).thenReturn( XDatas.from( xData ) );

        final ProcessCreateParams processCreateParams = Mockito.mock( ProcessCreateParams.class );
        final CreateContentParams createContentParams = CreateContentParams.create().
            parent( ContentPath.ROOT ).
            contentData( data ).extraDatas( ExtraDatas.create().
            add( new ExtraData( XDataName.from( "xDataName" ), extraData ) ).
            build() ).
            type( contentTypeName ).
            build();

        contentType = ContentType.create( contentType ).xData( XDataNames.from( XDataName.from( "xDataName" ) ) ).build();

        Mockito.when( contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) ) ).thenReturn( contentType );
        Mockito.when( processCreateParams.getCreateContentParams() ).thenReturn( createContentParams );

        final ProcessCreateResult result = htmlAreaContentProcessor.processCreate( processCreateParams );

        assertEquals( 2, result.getCreateContentParams().getProcessedIds().getSize() );
        assertTrue( result.getCreateContentParams().getProcessedIds().contains( ContentId.from( "image-id1" ) ) );
        assertTrue( result.getCreateContentParams().getProcessedIds().contains( ContentId.from( "image-id2" ) ) );
    }
}
