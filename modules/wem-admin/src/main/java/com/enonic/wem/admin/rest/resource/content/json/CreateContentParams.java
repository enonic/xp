package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class CreateContentParams
{
    private Boolean temporary = Boolean.FALSE;

    private String contentName;

    private ContentPath parentContentPath;

    private ContentTypeName qualifiedContentTypeName;

    private List<DataJson> contentData;

    private FormJson form;

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

    public FormJson getForm()
    {
        return form;
    }

    public void setForm( final FormJson form )
    {
        this.form = form;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public void setParentContentPath( final String parentContentPath )
    {
        this.parentContentPath = parentContentPath != null ? ContentPath.from( parentContentPath ) : null;
    }

    public ContentTypeName getQualifiedContentTypeName()
    {
        return qualifiedContentTypeName;
    }

    public void setQualifiedContentTypeName( final String qualifiedContentTypeName )
    {
        this.qualifiedContentTypeName = qualifiedContentTypeName != null ? ContentTypeName.from( qualifiedContentTypeName ) : null;
    }

    public List<DataJson> getContentData()
    {
        return contentData;
    }

    public void setContentData( final List<DataJson> contentData )
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
