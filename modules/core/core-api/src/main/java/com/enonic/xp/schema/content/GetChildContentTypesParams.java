package com.enonic.xp.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class GetChildContentTypesParams
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetChildContentTypesParams ) )
        {
            return false;
        }

        final GetChildContentTypesParams that = (GetChildContentTypesParams) o;
        return Objects.equals( this.parentName, that.parentName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.parentName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.parentName, "Parent content type name cannot be null" );
    }
}
