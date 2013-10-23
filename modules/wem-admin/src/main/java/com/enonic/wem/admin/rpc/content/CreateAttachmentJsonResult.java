package com.enonic.wem.admin.rpc.content;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;

final class CreateAttachmentJsonResult
    extends JsonResult
{
    private final ContentId contentId;

    private final ContentPath contentPath;

    private final List<Attachment> attachments;

    public CreateAttachmentJsonResult( final ContentSelector contentSelector, final List<Attachment> attachments )
    {
        if ( contentSelector instanceof ContentId )
        {
            this.contentId = (ContentId) contentSelector;
            this.contentPath = null;
        }
        else
        {
            this.contentId = null;
            this.contentPath = (ContentPath) contentSelector;
        }
        this.attachments = ImmutableList.copyOf( attachments );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        if ( contentId != null )
        {
            json.put( "contentId", contentId.toString() );
        }
        if ( contentPath != null )
        {
            json.put( "contentPath", contentPath.toString() );
        }
        final ArrayNode attachmentsJson = json.putArray( "attachments" );
        for ( Attachment attachment : this.attachments )
        {
            final ObjectNode attachmentJson = objectNode();
            attachmentJson.put( "name", attachment.getName() );
            attachmentJson.put( "size", attachment.getSize() );
            attachmentJson.put( "mimeType", attachment.getMimeType() );
            attachmentsJson.add( attachmentJson );
        }
    }
}
