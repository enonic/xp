package com.enonic.xp.schema.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetContentTypesParams
{
    private ContentTypeNames contentTypeNames;

    private boolean inlineMixinsToFormItems = false;

    public ContentTypeNames getContentTypeNames()
    {
        return this.contentTypeNames;
    }

    public GetContentTypesParams contentTypeNames( final ContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    public boolean isInlineMixinsToFormItems()
    {
        return inlineMixinsToFormItems;
    }

    public GetContentTypesParams inlineMixinsToFormItems( final boolean value )
    {
        inlineMixinsToFormItems = value;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof GetContentTypesParams ) )
        {
            return false;
        }

        final GetContentTypesParams that = (GetContentTypesParams) o;
        return Objects.equals( this.contentTypeNames, that.contentTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.contentTypeNames );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeNames, "contentTypeNames cannot be null" );
    }
}
