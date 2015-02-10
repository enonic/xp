package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.GetContentTypesParams;

final class GetContentTypesCommand
    extends AbstractCommand
{
    protected GetContentTypesParams params;

    public ContentTypes execute()
    {
        this.params.validate();
        return doExecute();
    }

    private ContentTypes doExecute()
    {
        final ContentTypes contentTypes = getContentTypes( this.params.getContentTypeNames() );
        if ( !this.params.isInlineMixinsToFormItems() )
        {
            return contentTypes;
        }
        else
        {
            return transformInlineMixins( contentTypes );
        }
    }

    private ContentTypes getContentTypes( final ContentTypeNames contentTypeNames )
    {
        final ContentTypes.Builder contentTypes = ContentTypes.newContentTypes();
        for ( final ContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType contentType = this.registry.get( contentTypeName );
            if ( contentType != null )
            {
                contentTypes.add( contentType );
            }
        }
        return contentTypes.build();
    }
}
