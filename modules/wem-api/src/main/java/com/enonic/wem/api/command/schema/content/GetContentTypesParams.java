package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class GetContentTypesParams
{
    private ContentTypeNames contentTypeNames;

    private boolean mixinReferencesToFormItems = false;

    public ContentTypeNames getContentTypeNames()
    {
        return this.contentTypeNames;
    }

    public GetContentTypesParams contentTypeNames( final ContentTypeNames contentTypeNames )
    {
        this.contentTypeNames = contentTypeNames;
        return this;
    }

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetContentTypesParams mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
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
        return Objects.equal( this.contentTypeNames, that.contentTypeNames );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.contentTypeNames );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeNames, "contentTypeNames cannot be null" );
    }
}
