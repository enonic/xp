package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.GetContentTypeParams;

final class GetContentTypeCommand
    extends AbstractCommand
{
    protected GetContentTypeParams params;

    public ContentType execute()
    {
        this.params.validate();
        return doExecute();
    }

    private ContentType doExecute()
    {
        final ContentType contentType = this.registry.get( this.params.getContentTypeName() );
        if ( contentType == null )
        {
            return null;
        }

        if ( !this.params.isInlineMixinsToFormItems() )
        {
            return contentType;
        }
        else
        {
            return transformInlineMixins( contentType );
        }
    }
}
