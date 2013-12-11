package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;

public class AttachmentJson
{
    private final Attachment attachment;

    @JsonCreator
    public AttachmentJson( @JsonProperty("blobKey") final String blobKeyAsString,
                           @JsonProperty("attachmentName") final String attachmentNameAsString,
                           @JsonProperty("mimeType") final String mimeType,
                           @JsonProperty("size") final String sizeAsString )
    {
        this.attachment = Attachment.newAttachment().
            blobKey( new BlobKey( blobKeyAsString ) ).
            size( Long.valueOf( sizeAsString ) ).
            name( attachmentNameAsString ).
            mimeType( mimeType ).
            build();
    }

    @JsonIgnore
    public Attachment getAttachment()
    {
        return attachment;
    }
}