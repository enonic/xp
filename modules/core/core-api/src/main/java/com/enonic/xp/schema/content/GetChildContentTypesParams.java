package com.enonic.xp.schema.content;

import static java.util.Objects.requireNonNull;


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
        requireNonNull( this.parentName, "parentName is required" );
    }
}
