package com.enonic.xp.lib.content.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class AttachmentsMapperTest
{
    @Test
    void allFieldsPopulated()
    {
        final Attachment attachment = Attachment.create()
            .name( "report.pdf" )
            .label( "Report" )
            .mimeType( "application/pdf" )
            .size( 1234L )
            .sha512( "deadbeef" )
            .build();

        final JsonNode node = serialize( Attachments.from( attachment ) ).get( "report.pdf" );

        assertThat( node.get( "name" ).asText() ).isEqualTo( "report.pdf" );
        assertThat( node.get( "label" ).asText() ).isEqualTo( "Report" );
        assertThat( node.get( "size" ).asLong() ).isEqualTo( 1234L );
        assertThat( node.get( "mimeType" ).asText() ).isEqualTo( "application/pdf" );
        assertThat( node.get( "sha512" ).asText() ).isEqualTo( "deadbeef" );
        assertThat( node.has( "textContent" ) ).isFalse();
    }

    @Test
    void sha512NullEmittedAsNull()
    {
        final Attachment attachment = Attachment.create()
            .name( "image.png" )
            .mimeType( "image/png" )
            .size( 42L )
            .build();

        final JsonNode node = serialize( Attachments.from( attachment ) ).get( "image.png" );

        assertThat( node.has( "sha512" ) ).isTrue();
        assertThat( node.get( "sha512" ).isNull() ).isTrue();
    }

    private JsonNode serialize( final Attachments attachments )
    {
        final JsonMapGenerator generator = new JsonMapGenerator();
        new AttachmentsMapper( attachments ).serialize( generator );
        return (JsonNode) generator.getRoot();
    }
}
