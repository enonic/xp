package com.enonic.xp.core.impl.content.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.media.ExtractedTextInfo;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;

import static org.junit.Assert.*;

public class TextExtractorContentProcessorTest
{

    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private TextExtractorContentProcessor processor;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.mixinService = Mockito.mock( MixinService.class );

        processor = new TextExtractorContentProcessor();
        processor.setContentTypeService( contentTypeService );
        processor.setMixinService( mixinService );
    }

    @Test
    public void process_create()
        throws Exception
    {
        Mockito.when( mixinService.getByContentType( Mockito.any() ) ).
            thenReturn( Mixins.create().
                add( Mixin.create().
                    name( "testMixin" ).
                    addFormItem( Input.create().
                        inputType( InputTypeName.TEXT_AREA ).
                        name( MediaInfo.EXTRACTED_TEXT_CONTENT ).
                        label( "hei" ).
                        build() ).
                    build() ).
                build() );

        final CreateContentParams params = CreateContentParams.create().
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            contentData( new PropertyTree() ).
            type( ContentTypeName.documentMedia() ).
            build();

        final String extractedText = "This is the extracted text";
        final ProcessCreateResult result = this.processor.processCreate( new ProcessCreateParams( params, MediaInfo.create().
            setExtratedTextInfo( new ExtractedTextInfo( extractedText ) ).
            build() ) );

        final ExtraDatas extraDatas = result.getCreateContentParams().getExtraDatas();
        final ExtraData testMixinData = extraDatas.getMetadata( MixinName.from( "testMixin" ) );

        assertEquals( extractedText, testMixinData.getData().getValue( MediaInfo.EXTRACTED_TEXT_CONTENT ).asString() );
    }


}