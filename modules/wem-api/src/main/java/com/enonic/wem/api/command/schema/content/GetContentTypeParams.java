package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.content.ContentTypeName;

public class GetContentTypeParams
{
    private ContentTypeName contentTypeName;

    private boolean mixinReferencesToFormItems = false;

    private boolean notFoundAsException = false;

    public ContentTypeName getContentTypeName()
    {
        return this.contentTypeName;
    }

    public GetContentTypeParams contentTypeName( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    public GetContentTypeParams notFoundAsException()
    {
        notFoundAsException = true;
        return this;
    }

    public GetContentTypeParams notFoundAsNull()
    {
        notFoundAsException = false;
        return this;
    }

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetContentTypeParams mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "contentTypeName cannot be null" );
    }

    public boolean isNotFoundAsException()
    {
        return notFoundAsException;
    }
}
