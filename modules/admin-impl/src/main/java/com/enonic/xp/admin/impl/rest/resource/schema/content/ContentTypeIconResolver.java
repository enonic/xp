package com.enonic.xp.admin.impl.rest.resource.schema.content;


import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

public final class ContentTypeIconResolver
{
    private final ContentTypeService contentTypeService;

    public ContentTypeIconResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public Icon resolveIcon( final ContentTypeName contentTypeName )
    {
        return findContentTypeIcon( contentTypeName );
    }

    public Icon resolveIcon( final ContentType contentType )
    {
        if ( contentType.getIcon() != null )
        {
            return contentType.getIcon();
        }
        else if ( contentType.hasSuperType() )
        {
            return findContentTypeIcon( contentType.getSuperType() );
        }
        else
        {
            return null;
        }
    }

    private Icon findContentTypeIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null && contentType.hasSuperType() )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType != null ? contentType.getIcon() : null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentTypeName );
        return contentTypeService.getByName( params );
    }
}
