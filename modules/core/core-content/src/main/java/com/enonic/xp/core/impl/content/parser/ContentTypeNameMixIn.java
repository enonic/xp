package com.enonic.xp.core.impl.content.parser;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.schema.content.ContentTypeName;

abstract class ContentTypeNameMixIn
{
    @JsonCreator
    public static ContentTypeName from( String value )
    {
        return ContentTypeName.from( value );
    }
}
