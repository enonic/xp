package com.enonic.wem.repo.internal.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.node.Attachment;

final class AttachmentJson
{
    @JsonProperty("blobKey")
    private String blobKey;

    @JsonProperty("attachmentName")
    private String attachmentName;

    @JsonProperty("mimeType")
    private String mimeType;

    @JsonProperty("size")
    private long size;

    public Attachment fromJson()
    {
        return Attachment.newAttachment().
            name( this.attachmentName ).
            size( this.size ).
            mimeType( this.mimeType ).
            blobKey( new BlobKey( this.blobKey ) ).
            build();
    }

    public static AttachmentJson toJson( final Attachment attachment )
    {
        final AttachmentJson json = new AttachmentJson();
        json.blobKey = attachment.blobKey().toString();
        json.attachmentName = attachment.name();
        json.mimeType = attachment.mimeType();
        json.size = attachment.size();
        return json;
    }
}
