package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.enonic.xp.schema.content.ContentTypeName;

public abstract class ContentTypeNameMixin
{
    @JsonCreator
    public static ContentTypeName from( String value )
    {
        return ContentTypeName.from( value );
    }
}
