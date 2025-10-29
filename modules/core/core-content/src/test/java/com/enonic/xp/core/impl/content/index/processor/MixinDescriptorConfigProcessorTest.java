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
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinName;

import static com.enonic.xp.content.ContentPropertyNames.MIXINS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MixinDescriptorConfigProcessorTest
{
    private ContentTypeService contentTypeService;

    private ContentTypeName contentTypeName;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.contentTypeName = ContentTypeName.folder();
    }

    @Test
    void test_data()
    {
        final PatternIndexConfigDocument result = processForms( Form.empty() );

        assertEquals( 1, result.getPathIndexConfigs().size() );
        assertEquals( IndexConfig.BY_TYPE, result.getConfigForPath( IndexPath.from( MIXINS ) ) );

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
                      result.getConfigForPath( IndexPath.from( MIXINS + ".appname.localname0.htmlarea" ) ).getIndexValueProcessors().get(
                          0 ).getName() );

    }

    private PatternIndexConfigDocument processForms( final Form... forms )
    {
        final MixinDescriptors.Builder builder = MixinDescriptors.create();

        for ( int i = 0; i < forms.length; i++ )
        {
            builder.add( MixinDescriptor.create().form( forms[i] ).name( MixinName.from( "appName:localName" + i ) ).build() );
        }

        final MixinDescriptors descriptors = builder.build();

        final ContentType contentType = ContentType.create().superType( ContentTypeName.folder() ).name( "contentType" ).build();

        Mockito.when( contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ) ).thenReturn(
            contentType );

        final MixinConfigProcessor configProcessor = new MixinConfigProcessor( descriptors );

        return configProcessor.processDocument( PatternIndexConfigDocument.create() ).build();
    }
}
