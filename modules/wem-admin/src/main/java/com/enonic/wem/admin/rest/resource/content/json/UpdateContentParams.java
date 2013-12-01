package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class UpdateContentParams
{
    private ContentId contentId;

    private String contentName;

    private ContentTypeName contentType;

    private List<DataJson> contentData;

    private FormJson form;

    private String displayName;

    private List<AttachmentJson> attachments;

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

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public void setContentType( final String value )
    {
        this.contentType = ContentTypeName.from( value );
    }

    public void setContentData( final List<DataJson> contentData )
    {
        this.contentData = contentData;
    }

    public List<DataJson> getContentData()
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

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public List<AttachmentJson> getAttachments()
    {
        return attachments;
    }

    public void setAttachments( final List<AttachmentJson> attachmentParams )
    {
        this.attachments = attachmentParams;
    }
}
