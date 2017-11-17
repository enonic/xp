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

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentTypeName = ContentTypeName.folder();
    }

    @Test
    public void test_data()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForm(Form.create().build());

        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( DATA ) ) );

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

        final PatternIndexConfigDocument result = processForm(form);


        assertEquals( 2, result.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper",
                      result.getConfigForPath( PropertyPath.from( DATA + ".htmlArea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }

    private Form getDataForm( final ContentTypeService contentTypeService, final ContentTypeName contentTypeName )
    {
        return contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

    private PatternIndexConfigDocument processForm( final Form form )
    {
        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "typeName" ).form( form ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        final DataConfigProcessor configProcessor =
            new DataConfigProcessor( getDataForm( contentTypeService, contentTypeName) );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
