package com.enonic.xp.lib.content.mapper;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentMapperTest
{
    @Test
    void language_with_region()
    {
        final Content content = Content.create()
            .id( ContentId.from( "123" ) )
            .name( "test" )
            .displayName( "Test" )
            .parentPath( ContentPath.ROOT )
            .language( Locale.forLanguageTag( "af-Latn-ZA" ) )
            .data( new PropertyTree() )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) )
            .build();

        final JsonMapGenerator generator = new JsonMapGenerator();
        new ContentMapper( content ).serialize( generator );

        final JsonNode json = (JsonNode) generator.getRoot();
        assertEquals( "af-Latn-ZA", json.get( "language" ).asText() );
    }

    @Test
    void language_null()
    {
        final Content content = Content.create()
            .id( ContentId.from( "123" ) )
            .name( "test" )
            .displayName( "Test" )
            .parentPath( ContentPath.ROOT )
            .data( new PropertyTree() )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) )
            .build();

        final JsonMapGenerator generator = new JsonMapGenerator();
        new ContentMapper( content ).serialize( generator );

        final JsonNode json = (JsonNode) generator.getRoot();
        assertTrue( json.get( "language" ).isNull() );
    }

    @Test
    void validationErrors_errorCodeIsSerializedAsObject()
    {
        final ValidationErrorCode code = ValidationErrorCode.from( ApplicationKey.from( "com.enonic.myapp" ), "REQUIRED" );
        final Content content =
            newMinimalContent().validationErrors( ValidationErrors.create().add( ValidationError.generalError( code ).build() ).build() )
                .build();

        final JsonNode error = serialize( content ).get( "validationErrors" ).get( 0 );

        assertThat( error.get( "errorCode" ).isObject() ).isTrue();
        assertThat( error.get( "errorCode" ).get( "applicationKey" ).asText() ).isEqualTo( "com.enonic.myapp" );
        assertThat( error.get( "errorCode" ).get( "code" ).asText() ).isEqualTo( "REQUIRED" );
    }

    private Content.Builder newMinimalContent()
    {
        return Content.create()
            .id( ContentId.from( "123" ) )
            .name( "test" )
            .displayName( "Test" )
            .parentPath( ContentPath.ROOT )
            .data( new PropertyTree() )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) );
    }

    private JsonNode serialize( final Content content )
    {
        final JsonMapGenerator generator = new JsonMapGenerator();
        new ContentMapper( content ).serialize( generator );
        return (JsonNode) generator.getRoot();
    }
}
