package com.enonic.xp.portal.impl.processor;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
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

import static org.junit.Assert.*;

public class HtmlAreaContentProcessorTest
{
    private ProcessUpdateResult result;

    private HtmlAreaContentProcessor htmlAreaContentProcessor;

    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    private ContentTypeName contentTypeName;

    @Before
    public void setUp()
        throws Exception
    {

        this.siteService = Mockito.mock( SiteService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeService = Mockito.mock( ContentTypeService.class );

        contentTypeName = ContentTypeName.from( "myContentType" );

        final GetContentTypeParams params = GetContentTypeParams.from( contentTypeName );
        final ContentType contentType = ContentType.create().
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
        htmlAreaContentProcessor.setxDataService( xDataService );

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
    public void supports()
        throws IOException
    {
        assertTrue( new HtmlAreaContentProcessor().supports( ContentType.create().
            name( contentTypeName ).
            superType( ContentTypeName.folder() ).
            build() ) );
    }

    @Test
    public void create()
        throws IOException
    {
        final ProcessCreateParams processCreateParams = Mockito.mock( ProcessCreateParams.class );
        final CreateContentParams createContentParams = CreateContentParams.create().
            parent( ContentPath.ROOT ).
            contentData( new PropertyTree() ).
            type( contentTypeName ).
            build();

        Mockito.when( processCreateParams.getCreateContentParams() ).thenReturn( createContentParams );

        final ProcessCreateResult result = new HtmlAreaContentProcessor().processCreate( processCreateParams );

        assertEquals( createContentParams.getParent(), result.getCreateContentParams().getParent() );
        assertEquals( createContentParams.getData(), result.getCreateContentParams().getData() );
        assertEquals( createContentParams.getType(), result.getCreateContentParams().getType() );
    }
}
