package com.enonic.wem.admin.json.content.attachment;

import org.h2.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;

public class AttachmentJson
{
    private final Attachment attachment;

    @JsonCreator
    public AttachmentJson( @JsonProperty("blobKey") final String blobKeyAsString, //
                           @JsonProperty("attachmentName") final String attachmentNameAsString, //
                           @JsonProperty("mimeType") final String mimeType, //
                           @JsonProperty("size") final String sizeAsString )
    {
        this.attachment = Attachment.newAttachment().
            blobKey( new BlobKey( blobKeyAsString ) ).
            size( StringUtils.isNullOrEmpty( sizeAsString ) ? 0 : Long.valueOf( sizeAsString ) ).
            name( attachmentNameAsString ).
            mimeType( mimeType ).
            build();
    }

    public AttachmentJson( final Attachment attachment )
    {
        this.attachment = attachment;
    }

    public String getBlobKey()
    {
        return this.attachment.getBlobKey().toString();
    }

    public String getAttachmentName()
    {
        return this.attachment.getName();
    }

    public String getMimeType()
    {
        return this.attachment.getMimeType();
    }

    public long getSize()
    {
        return this.attachment.getSize();
    }

    @JsonIgnore
    public Attachment getAttachment()
    {
        return attachment;
    }
}