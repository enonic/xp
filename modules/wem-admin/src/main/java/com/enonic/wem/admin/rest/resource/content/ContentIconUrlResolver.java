package com.enonic.wem.admin.rest.resource.content;

import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.servlet.ServletRequestUrlHelper;

public final class ContentIconUrlResolver
{
    private ContentTypeService contentTypeService;

    private ContentTypeIconResolver contentTypeIconResolver;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    public ContentIconUrlResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( this.contentTypeIconResolver );
    }

    private String getImageAttachmentName( final Content content )
    {
        final PropertyTree contentData = content.getData();
        final String image = contentData.getString( "image" );
        if ( image == null )
        {
            return content.getName().toString();
        }
        return image;
    }

    public String resolve( final Content content )
    {
        if ( content.hasThumbnail() )
        {
            return ServletRequestUrlHelper.createUri(
                "/admin/rest/content/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
        }
        else if ( content.getType().isImageMedia() )
        {
            final String attachmentName = getImageAttachmentName( content );
            final Attachment attachment = content.getAttachments().getAttachment( attachmentName );
            if ( attachment != null )
            {
                return ServletRequestUrlHelper.createUri(
                    "/admin/rest/content/icon/" + content.getId() + "?ts=" + content.getModifiedTime().toEpochMilli() );
            }
            else
            {
                return this.contentTypeIconUrlResolver.resolve(
                    this.contentTypeService.getByName( GetContentTypeParams.from( ContentTypeName.imageMedia() ) ) );
            }
        }
        return this.contentTypeIconUrlResolver.resolve( content.getType() );
    }
}
