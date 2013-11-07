package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class GetContentType
    extends Command<ContentType>
{
    private ContentTypeName contentTypeName;

    private boolean mixinReferencesToFormItems = false;

    public ContentTypeName getContentTypeName()
    {
        return this.contentTypeName;
    }

    public GetContentType contentTypeName( final ContentTypeName contentTypeName )
    {
        this.contentTypeName = contentTypeName;
        return this;
    }

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetContentType mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentTypeName, "contentTypeName cannot be null" );
    }
}
