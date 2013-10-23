package com.enonic.wem.admin.rest.resource.content.json;

public class AttachmentParams
{
    private String uploadId;

    private String attachmentName;

    public String getUploadId()
    {
        return uploadId;
    }

    public void setUploadId( final String uploadId )
    {
        this.uploadId = uploadId;
    }

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
    }
}