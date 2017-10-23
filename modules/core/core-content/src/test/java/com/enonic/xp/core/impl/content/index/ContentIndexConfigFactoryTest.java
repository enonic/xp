package com.enonic.xp.core.impl.content.index;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class ContentIndexConfigFactoryTest
{
    protected ContentTypeService contentTypeService;

    protected PageDescriptorService pageDescriptorService;

    @Before
    public void setUp()
        throws Exception
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
        pageDescriptorService = Mockito.mock( PageDescriptorService.class );
    }

    @Test
    public void media_indexing()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "myStringValue" );
        final PropertySet metadata = data.addSet( ContentPropertyNames.METADATA );
        metadata.addString( "media", "imageMedia" );
        metadata.addDouble( "double", 13d );
        metadata.addString( "no-index", "no-index-value" );
        final PropertySet subSet = metadata.addSet( "subSet" );
        subSet.addString( "subSetValue", "promp" );

        final Form form = Form.create().build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form( form ).build();
        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create().
            contentTypeName( ContentTypeName.imageMedia() ).
            contentTypeService( contentTypeService ).
            build().produce();

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "media" ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet" ) ) );

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet", "subSetValue" ) ) );
    }

    @Test
    public void html_area_path()
        throws Exception
    {
        final Input htmlInput1 = Input.create().name( "myHtmlArea" ).label( "HtmlArea" ).inputType( InputTypeName.HTML_AREA ).build();

        final FormItemSet myOuterSet =
            FormItemSet.create().name( "myOuterSet" ).label( "Label" ).multiple( true ).addFormItem( htmlInput1 ).build();

        final Form form = Form.create().
            addFormItem( myOuterSet ).build();

        System.out.println( form );

        System.out.println( htmlInput1.getPath() );

        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form( form ).build();
        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create().
            contentTypeName( ContentTypeName.imageMedia() ).
            contentTypeService( contentTypeService ).
            build().produce();

        final IndexConfig configForPath = indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.DATA, htmlInput1.getPath().getElementsAsArray() ) );

        assertNotNull( configForPath );
        assertEquals( 1, configForPath.getIndexValueProcessors().size() );
    }

    @Test
    public void page_indexing()
        throws Exception
    {
        final Form pageForm = Form.create().
            addFormItem( FormItemSet.create().
                name( "region" ).
                addFormItem( FormItemSet.create().name( "textcomponent" ).
                    addFormItem( Input.create().name( "text" ).label( "text" ).inputType( InputTypeName.HTML_AREA ).build() ).
                    build() ).
                build() ).
            build();

        final ContentType contentType = ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form(
            Form.create().build() ).build();

        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        Mockito.when( pageDescriptorService.getByKey( DescriptorKey.from( "controllerName" ) ) ).
            thenReturn( PageDescriptor.create().
                key( DescriptorKey.from( "controllerName" ) ).
                config( pageForm ).regions( RegionDescriptors.create().build() ).build() );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create().
            contentTypeName( ContentTypeName.imageMedia() ).
            contentTypeService( contentTypeService ).
            pageDescriptorService( pageDescriptorService ).
            descriptorKey( DescriptorKey.from( "controllerName" ) ).
            build().produce();


        assertFalse( indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.PAGE ) ).isEnabled() );
        assertFalse( indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "PartComponent", "template" ) ).isEnabled() );

        final IndexConfig htmlAreaConfig = indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.PAGE, "region", "component", "TextComponent", "text" ) );

        assertEquals( htmlAreaConfig.getIndexValueProcessors().get( 0 ).getName(), "htmlStripper");
    }
}