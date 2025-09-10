package com.enonic.xp.content.validate;

import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentValidatorParamsTest
{
    @Test
    void builder()
    {
        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( "ct" ).superType( ContentTypeName.unstructured() ).build() )
            .contentId( ContentId.from( "ci" ) )
            .data( new PropertyTree() )
            .createAttachments(
                CreateAttachments.create().add( CreateAttachment.create().name( "att" ).byteSource( ByteSource.empty() ).build() ).build() )
            .extraDatas( ExtraDatas.create().add( new ExtraData( XDataName.from( "xd" ), new PropertyTree() ) ).build() )
            .build();
        assertEquals( params.getContentType().getName(), ContentTypeName.from( "ct" ) );
        assertEquals( params.getContentId(), ContentId.from( "ci" ) );
        assertNotNull( params.getData() );
        assertThat( params.getCreateAttachments() ).extracting( "name" ).containsExactly( "att" );
        assertThat( params.getExtraDatas() ).extracting( "name" ).containsExactly( XDataName.from( "xd" ) );
    }

    @Test
    void extraDatas_null_safe()
    {
        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( "ct" ).superType( ContentTypeName.unstructured() ).build() )
            .build();
        assertNotNull( params.getExtraDatas() );
    }

    @Test
    void createAttachments_null_safe()
    {
        final ContentValidatorParams params = ContentValidatorParams.create()
            .contentType( ContentType.create().name( "ct" ).superType( ContentTypeName.unstructured() ).build() )
            .build();
        assertNotNull( params.getCreateAttachments() );
    }
}
