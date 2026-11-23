package com.enonic.xp.core.impl.content.index;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentIndexConfigFactoryTest
{
    @Test
    void textual_media_text_is_fulltext_indexed_at_stored_path()
    {
        final IndexConfig config = produce( ContentTypeName.documentMedia() ).getConfigForPath( IndexPath.from( "data.media.text" ) );

        assertThat( config.isFulltext() ).isTrue();
        assertThat( config.isIncludeInAllText() ).isTrue();
        assertThat( config.isnGram() ).isTrue();
    }

    @Test
    void non_textual_media_text_is_indexed_by_type()
    {
        final IndexConfig config = produce( ContentTypeName.imageMedia() ).getConfigForPath( IndexPath.from( "data.media.text" ) );

        assertThat( config ).isEqualTo( IndexConfig.BY_TYPE );
    }

    private static IndexConfigDocument produce( final ContentTypeName contentTypeName )
    {
        final Form form = Form.create()
            .addFormItem( Input.create()
                              .name( ContentPropertyNames.MEDIA )
                              .label( "Media" )
                              .inputType( InputTypeName.MEDIA_UPLOADER )
                              .build() )
            .build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.media() ).name( contentTypeName ).form( form ).build();

        final ContentTypeService contentTypeService = mock( ContentTypeService.class );
        when( contentTypeService.getByName( any() ) ).thenReturn( contentType );

        return ContentIndexConfigFactory.create()
            .contentTypeService( contentTypeService )
            .contentTypeName( contentTypeName )
            .build()
            .produce();
    }
}
