package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.schema.content.ContentTypeName;

public abstract class ContentTypeNameMapper
{
    @JsonCreator
    public static ContentTypeName from( String value )
    {
        return ContentTypeName.from( value );
    }
}
