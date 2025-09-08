package com.enonic.xp.core.impl.content.parser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
        assertNotNull( contentType.getForm() );
        assertEquals( now, contentType.getCreatedTime() );
    }

    private String readAsString( final String name )
        throws Exception
    {
        return Files.readString( Paths.get( YmlContentTypeParserTest.class.getResource( name ).toURI() ), StandardCharsets.UTF_8 );
    }
}
