package com.enonic.wem.admin.rest.resource.content;

import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.wem.admin.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.api.content.attachment.GetAttachmentParameters;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.servlet.ServletRequestUrlHelper;

public final class ContentIconUrlResolver
{
    protected final static Context STAGE_CONTEXT = Context.create().
        workspace( ContentConstants.WORKSPACE_STAGE ).
        repository( ContentConstants.CONTENT_REPO ).
        build();


    private ContentTypeService contentTypeService;

    private AttachmentService attachmentService;

    private ContentTypeIconResolver contentTypeIconResolver;

    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    public ContentIconUrlResolver( final ContentTypeService contentTypeService, final AttachmentService attachmentService )
    {
        this.contentTypeService = contentTypeService;
        this.contentTypeIconResolver = new ContentTypeIconResolver( contentTypeService );
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( this.contentTypeIconResolver );
        this.attachmentService = attachmentService;
    }

    private String getImageAttachmentName( final Content content )
    {
        final ContentData contentData = content.getContentData();
        final Property imageProperty = contentData.getProperty( "image" );
        return imageProperty.hasNullValue() ? content.getName().toString() : imageProperty.getString();
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
            final Attachment attachment = attachmentService.get( GetAttachmentParameters.create().
                contentId( content.getId() ).
                attachmentName( attachmentName ).
                context( STAGE_CONTEXT ).
                build() );
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
