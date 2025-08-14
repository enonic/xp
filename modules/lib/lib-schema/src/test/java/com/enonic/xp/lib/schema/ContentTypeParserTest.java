package com.enonic.xp.lib.schema;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentTypeParserTest
{
    @Test
    void testParse()
        throws Exception
    {
        final String yaml = new String( ContentTypeParserTest.class.getResourceAsStream( "/descriptors/mycontenttype.yml" ).readAllBytes(),
                                        StandardCharsets.UTF_8 );

        ContentTypeParser parser = new ContentTypeParser();
        ContentType contentType = parser.parse( yaml );

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( ContentTypeName.from( "base:structured" ), contentType.getSuperType() );
        assertFalse( contentType.isAbstract() );
        assertFalse( contentType.isFinal() );
        assertTrue( contentType.allowChildContent() );
        assertFalse( contentType.isBuiltIn() );
        assertEquals( "Article heading", contentType.getDisplayNameLabel() );
        assertEquals( "article.title", contentType.getDisplayNameLabelI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
    }
}
