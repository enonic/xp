package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.StringPropertyValue;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class YmlContentTypeParserTest
{
    @Test
    void testParse()
        throws Exception
    {
        final String yaml = readAsString( "/descriptors/content-type.yml" );

        final ApplicationKey myapp = ApplicationKey.from( "myapp" );
        ContentType.Builder builder = YmlContentTypeParser.parse( yaml, myapp );
        builder.name( ContentTypeName.from( myapp, "article" ) );

        final Instant now = Instant.now();
        builder.createdTime( now );

        final ContentType contentType = builder.build();

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( "Article", contentType.getDisplayName() );
        assertEquals( "i18n.article.displayName", contentType.getDisplayNameI18nKey() );
        assertNull( contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
        assertNotNull( contentType.getSchemaConfig() );
        assertEquals( "${expression}", contentType.getSchemaConfig()
            .getProperty( "displayNameExpression" )
            .map( InputTypeProperty::getValue )
            .filter( StringPropertyValue.class::isInstance )
            .map( StringPropertyValue.class::cast )
            .map( StringPropertyValue::value )
            .orElse( null ) );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlContentTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
