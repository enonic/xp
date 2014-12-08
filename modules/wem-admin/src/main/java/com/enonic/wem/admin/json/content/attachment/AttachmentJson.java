package com.enonic.wem.admin.json.content.attachment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.attachment.Attachment;

public class AttachmentJson
{
    private final Attachment attachment;

    @JsonCreator
    public AttachmentJson( @JsonProperty("blobKey") final String blobKeyAsString, //
                           @JsonProperty("name") final String nameAsString, //
                           @JsonProperty("label") final String labelAsString, //
                           @JsonProperty("mimeType") final String mimeType, //
                           @JsonProperty("size") final String sizeAsString )
    {
        this.attachment = Attachment.newAttachment().
            blobKey( new BlobKey( blobKeyAsString ) ).
            size( Strings.isNullOrEmpty( sizeAsString ) ? 0 : Long.valueOf( sizeAsString ) ).
            name( nameAsString ).
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

    public String getName()
    {
        return this.attachment.getName();
    }

    public String getLabel()
    {
        return this.attachment.getLabel();
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