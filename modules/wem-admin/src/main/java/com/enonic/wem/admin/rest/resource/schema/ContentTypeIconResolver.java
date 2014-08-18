package com.enonic.wem.admin.rest.resource.schema;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;

public class ContentTypeIconResolver
{
    private ContentTypeService contentTypeService;

    public ContentTypeIconResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public Icon resolve( ContentTypeName contentTypeName )
    {
        return findContentTypeIcon( contentTypeName );
    }

    private Icon findContentTypeIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        if ( contentType == null )
        {
            return null;
        }
        else if ( contentType.getIcon() != null )
        {
            return contentType.getIcon();
        }

        do
        {
            contentType = getContentType( contentType.getSuperType() );
            if ( contentType.getIcon() != null )
            {
                return contentType.getIcon();
            }
        }
        while ( contentType != null );
        return null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentTypeName );

        return contentTypeService.getByName( params );
    }
}
