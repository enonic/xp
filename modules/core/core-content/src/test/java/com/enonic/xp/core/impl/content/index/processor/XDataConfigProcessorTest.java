package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;

import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XDataConfigProcessorTest
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private ContentTypeName contentTypeName;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.xDataService = Mockito.mock( XDataService.class );
        this.contentTypeName = ContentTypeName.folder();
    }

    @Test
    public void test_data()
        throws Exception
    {
        final PatternIndexConfigDocument result = processForms( Form.create().build() );

        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( PropertyPath.from( EXTRA_DATA ) ) );

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

        final PatternIndexConfigDocument result = processForms( form );

        assertEquals( 2, result.getPathIndexConfigs().size() );
        assertEquals( "htmlStripper",
                      result.getConfigForPath( PropertyPath.from( EXTRA_DATA + ".appname.localname0.htmlarea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }

    private PatternIndexConfigDocument processForms( final Form... forms )
    {
        final XDatas.Builder xDatasBuilder = XDatas.create();

        for ( int i = 0; i < forms.length; i++ )
        {
            xDatasBuilder.add( XData.create().form( forms[i] ).name( XDataName.from( "appName:localName" + i ) ).build() );
        }

        final XDatas xDatas = xDatasBuilder.build();

        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "contentType" ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        final XDataConfigProcessor configProcessor = new XDataConfigProcessor( xDatas );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
