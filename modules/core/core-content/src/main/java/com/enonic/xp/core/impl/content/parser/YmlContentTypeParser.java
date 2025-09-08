package com.enonic.xp.core.impl.content.parser;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

public final class YmlContentTypeParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ContentTypeName.class, ContentTypeNameMixIn.class );
        PARSER.addMixIn( ContentType.Builder.class, ContentTypeBuilderMixIn.class );
    }

    public static ContentType.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        final ContentType.Builder builder = PARSER.parse( resource, ContentType.Builder.class, currentApplication );
        builder.name( "_TEMP_NAME_" );

        final ContentType contentType = builder.build();

        return ContentType.create( contentType )
            .superType( new ApplicationRelativeResolver( currentApplication ).toContentTypeName( contentType.getSuperType().toString() ) );
    }

}
