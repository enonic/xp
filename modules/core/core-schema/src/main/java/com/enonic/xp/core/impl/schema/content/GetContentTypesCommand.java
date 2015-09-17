package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetContentTypesParams;

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
        final ContentTypes.Builder contentTypes = ContentTypes.create();
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
