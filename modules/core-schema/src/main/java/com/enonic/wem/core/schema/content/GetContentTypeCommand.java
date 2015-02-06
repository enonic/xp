package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

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

        if ( !this.params.isInlinesToFormItems() )
        {
            return contentType;
        }
        else
        {
            return transformInlines( contentType );
        }
    }
}
