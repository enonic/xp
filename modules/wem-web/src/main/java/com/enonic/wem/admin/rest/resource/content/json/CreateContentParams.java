package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

public class CreateContentParams
{
    private Boolean temporary;

    private String contentName;

    private ContentPath parentContentPath;

    private QualifiedContentTypeName qualifiedContentTypeName;

    private JsonNode contentData;

    private String displayName;

    private List<AttachmentParams> attachments;


    public Boolean getTemporary()
    {
        return temporary;
    }

    public void setTemporary( final String temporary )
    {
        this.temporary = Boolean.valueOf( temporary );
    }

    public String getContentName()
    {
        return contentName;
    }

    public void setContentName( final String contentName )
    {
        this.contentName = contentName;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public void setParentContentPath( final String parentContentPath )
    {
        this.parentContentPath = ContentPath.from( parentContentPath );
    }

    public QualifiedContentTypeName getQualifiedContentTypeName()
    {
        return qualifiedContentTypeName;
    }

    public void setQualifiedContentTypeName( final String qualifiedContentTypeName )
    {
        this.qualifiedContentTypeName = QualifiedContentTypeName.from( qualifiedContentTypeName );
    }

    public JsonNode getContentData()
    {
        return contentData;
    }

    public void setContentData( final JsonNode contentData )
    {
        this.contentData = contentData;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public List<AttachmentParams> getAttachments()
    {
        return attachments;
    }

    public void setAttachments( final List<AttachmentParams> attachmentParams )
    {
        this.attachments = attachmentParams;
    }

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
}
