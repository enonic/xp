package com.enonic.xp.core.impl.content.index.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.schema.xdata.MixinService;
import com.enonic.xp.schema.xdata.MixinDescriptors;

import static com.enonic.xp.content.ContentPropertyNames.MIXIN_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XDataConfigProcessorTest
{
    private ContentTypeService contentTypeService;

    private MixinService xDataService;

    private ContentTypeName contentTypeName;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.xDataService = Mockito.mock( MixinService.class );
        this.contentTypeName = ContentTypeName.folder();
    }

    @Test
    void test_data()
    {
        final PatternIndexConfigDocument result = processForms( Form.empty() );

        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( IndexPath.from( MIXIN_DATA ) ) );

    }

    @Test
    void test_data_form_with_html_area()
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
                      result.getConfigForPath( IndexPath.from( MIXIN_DATA + ".appname.localname0.htmlarea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }

    private PatternIndexConfigDocument processForms( final Form... forms )
    {
        final MixinDescriptors.Builder xDatasBuilder = MixinDescriptors.create();

        for ( int i = 0; i < forms.length; i++ )
        {
            xDatasBuilder.add( MixinDescriptor.create().form( forms[i] ).name( MixinName.from( "appName:localName" + i ) ).build() );
        }

        final MixinDescriptors xDatas = xDatasBuilder.build();

        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "contentType" ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        final MixinConfigProcessor configProcessor = new MixinConfigProcessor( xDatas );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
