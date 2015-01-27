package com.enonic.xp.portal.impl.resource.image;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Media;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;

public final class ImageResource
    extends BaseSubResource
{
    // Update to use {id}/{name}  -> name is verified against content.getName()
    @Path("id/{id}")
    public ImageHandleResource imageById( @PathParam("id") final String id )
    {
        final ImageHandleResource resource = initResource( new ImageHandleResource() );

        final ContentId imageContentId = ContentId.from( id );
        final Media imageContent = getImage( imageContentId );

        resource.attachment = imageContent.getMediaAttachment();
        if ( resource.attachment == null )
        {
            throw notFound( "Attachment [%s] not found", imageContent.getName().toString() );
        }

        resource.binary = this.services.getContentService().getBinary( imageContentId, resource.attachment.getBinaryReference() );
        if ( resource.binary == null )
        {
            throw notFound( "Binary [%s] not found for content [%s]", resource.attachment.getBinaryReference(), imageContentId );
        }

        return resource;
    }

    // Do not use name as attachment name, just verify against content.getName()
    @Path("{name}")
    public ImageHandleResource imageByName( @PathParam("name") final String name )
    {
        final ImageHandleResource resource = initResource( new ImageHandleResource() );

        final Content content = getImage( this.contentPath );
        resource.attachment = content.getAttachments().byName( name );

        resource.binary = this.services.getContentService().getBinary( content.getId(), resource.attachment.getBinaryReference() );
        if ( resource.binary == null )
        {
            throw notFound( "Binary [%s] not found for content [%s]", resource.attachment.getBinaryReference(), content.getId() );
        }

        return resource;
    }

    private Media getImage( final ContentId contentId )
    {
        final Content content = this.services.getContentService().getById( contentId );
        if ( content == null )
        {
            throw notFound( "Content with id [%s] not found", contentId.toString() );
        }

        if ( !( content instanceof Media ) )
        {
            throw notFound( "Content with id [%s] is not an Image", contentId.toString() );
        }

        final Media media = (Media) content;
        if ( !media.isImage() )
        {
            throw notFound( "Content with id [%s] is not an Image", contentId.toString() );
        }

        return media;
    }

    private Content getImage( final ContentPath contentPath )
    {
        final Content content = this.services.getContentService().getByPath( contentPath );
        if ( content == null )
        {
            throw notFound( "Content with path [%s] not found", contentPath.toString() );
        }

        if ( !( content instanceof Media ) )
        {
            throw notFound( "Content with path [%s] is not an Image", contentPath.toString() );
        }

        final Media media = (Media) content;
        if ( !media.isImage() )
        {
            throw notFound( "Content with path [%s] is not an Image", contentPath.toString() );
        }

        return media;
    }
}
