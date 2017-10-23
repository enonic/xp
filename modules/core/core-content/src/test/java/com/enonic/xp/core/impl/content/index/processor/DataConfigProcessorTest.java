package com.enonic.xp.core.impl.content.index.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.content.ContentPropertyNames.DATA;

import static org.junit.Assert.*;

public class DataConfigProcessorTest
{
    private ContentTypeService contentTypeService;

    private ContentTypeName contentTypeName;

    private DataConfigProcessor configProcessor;

    private PatternIndexConfigDocument.Builder builder;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentTypeName = ContentTypeName.folder();
        this.configProcessor =
            DataConfigProcessor.create().contentTypeService( contentTypeService ).contentTypeName( contentTypeName ).build();
        builder = PatternIndexConfigDocument.create();
    }

    @Test
    public void test_empty_data_form()
        throws Exception
    {
        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "typeName" ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        configProcessor.processDocument( builder );

        assertEquals( 1, builder.build().getPathIndexConfigs().size() );
        assertEquals( IndexConfig.BY_TYPE, builder.build().getConfigForPath( PropertyPath.from( DATA ) ) );

    }

    @Test
    public void test_data_form_with_html_area()
        throws Exception
    {
        Input myTextLine = Input.create().
            name( "htmlArea" ).
            inputType( InputTypeName.HTML_AREA ).
            label( "htmlArea" ).
            required( true ).
            build();
        Form form = Form.create().
            addFormItem( myTextLine ).
            build();

        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "typeName" ).form( form ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        configProcessor.processDocument( builder );

        assertEquals( 2, builder.build().getPathIndexConfigs().size() );
        assertEquals( "htmlStripper",
                      builder.build().getConfigForPath( PropertyPath.from( DATA + ".htmlArea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }
}
