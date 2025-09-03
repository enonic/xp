package com.enonic.xp.core.impl.schema;

import com.fasterxml.jackson.databind.InjectableValues;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeBuilderMapper;
import com.enonic.xp.core.impl.schema.mapper.ContentTypeNameMapper;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

public final class YmlContentTypeParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ContentTypeName.class, ContentTypeNameMapper.class );
        PARSER.addMixIn( ContentType.Builder.class, ContentTypeBuilderMapper.class );
    }

    public static ContentType.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        final ApplicationRelativeResolver applicationRelativeResolver = new ApplicationRelativeResolver( currentApplication );

        final ContentType.Builder builder = PARSER.parse( resource, ContentType.Builder.class,
                                                          new InjectableValues.Std().addValue( "applicationRelativeResolver",
                                                                                               applicationRelativeResolver ) );
        builder.name( "_TEMP_NAME_" );

        final ContentType contentType = builder.build();

        return ContentType.create( contentType )
            .superType( applicationRelativeResolver.toContentTypeName( contentType.getSuperType().toString() ) );
    }

}
