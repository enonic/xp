package com.enonic.xp.schema.content;

import com.google.common.base.Preconditions;

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
        Preconditions.checkNotNull( this.parentName, "Parent content type name cannot be null" );
    }
}
