package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class UpdateContentParams
{
    private ContentId contentId;

    private String contentName;

    private ContentTypeName qualifiedContentTypeName;

    private JsonNode contentData;

    private FormJson form;

    private String displayName;

    private List<AttachmentParams> attachments;

    public ContentId getContentId()
    {
        return contentId;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }

    public String getContentName()
    {
        return contentName;
    }

    public void setContentName( final String contentName )
    {
        this.contentName = contentName;
    }

    public ContentTypeName getQualifiedContentTypeName()
    {
        return qualifiedContentTypeName;
    }

    public void setQualifiedContentTypeName( final String qualifiedContentTypeName )
    {
        this.qualifiedContentTypeName = ContentTypeName.from( qualifiedContentTypeName );
    }

    public JsonNode getContentData()
    {
        return contentData;
    }

    public FormJson getForm()
    {
        return form;
    }

    public void setForm( final FormJson form )
    {
        this.form = form;
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
}
