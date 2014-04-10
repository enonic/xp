package com.enonic.wem.core.entity.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.Attachment;

public class AttachmentJson
{
    private final Attachment attachment;

    @JsonCreator
    public AttachmentJson( @JsonProperty("blobKey") final String blobKey, //
                           @JsonProperty("attachmentName") final String attachmentName, //
                           @JsonProperty("mimeType") final String mimeType, //
                           @JsonProperty("size") final String size )
    {
        this.attachment = Attachment.newAttachment().
            name( attachmentName ).
            size( new Long( size ) ).
            mimeType( mimeType ).
            blobKey( new BlobKey( blobKey ) ).
            build();
    }

    public AttachmentJson( final Attachment attachment )
    {
        this.attachment = attachment;
    }

    public String getBlobKey()
    {
        return this.attachment.blobKey().toString();
    }

    public String getAttachmentName()
    {
        return this.attachment.name();
    }

    public String getMimeType()
    {
        return this.attachment.mimeType();
    }

    public long getSize()
    {
        return this.attachment.size();
    }

    @JsonIgnore
    public Attachment getAttachment()
    {
        return this.attachment;
    }
}
