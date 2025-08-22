package com.enonic.xp.schema.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetChildContentTypesParams
{
    private ContentTypeName parentName;

    public ContentTypeName getParentName()
    {
        return parentName;
    }

    public GetChildContentTypesParams parentName( final ContentTypeName parentName )
    {
        this.parentName = parentName;
        return this;
    }

    public void validate()
    {
        Objects.requireNonNull( this.parentName, "parentName is required" );
    }
}
