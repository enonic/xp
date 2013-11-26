package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.api.data.DataPath;

public class AttachmentParams
{
    private String uploadId;

    private AttachmentNameParams attachmentName;

    public String getUploadId()
    {
        return uploadId;
    }

    public void setUploadId( final String uploadId )
    {
        this.uploadId = uploadId;
    }

    public AttachmentNameParams getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName( final AttachmentNameParams attachmentName )
    {
        this.attachmentName = attachmentName;
    }


    public class AttachmentNameParams {

        private String fileName;
        private DataPath dataPath;

        public String getFileName()
        {
            return fileName;
        }

        public void setFileName( final String fileName )
        {
            this.fileName = fileName;
        }

        public DataPath getDataPath()
        {
            return dataPath;
        }

        public void setDataPath( final String dataPath )
        {
            this.dataPath = dataPath != null ? DataPath.from( dataPath ) : null;
        }
    }
}