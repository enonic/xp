package com.enonic.wem.admin.rest.resource.content.json;


import java.util.List;

import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.data.json.DataJson;

public class UpdateContentJson
{
    private ContentId contentId;

    private ContentName contentName;

    private ContentTypeName contentType;

    private List<DataJson> contentData;

    private FormJson form;

    private String displayName;

    private UpdateAttachmentsJson updateAttachments;

    private ThumbnailJson thumbnail;

    private String draft;

    public boolean isDraft()
    {
        return Boolean.parseBoolean( draft );
    }

    public void setDraft( final String draft )
    {
        this.draft = draft;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = ContentId.from( contentId );
    }

    public ContentName getContentName()
    {
        return contentName;
    }

    public void setContentName( final String contentName )
    {
        this.contentName = ContentName.from( contentName );
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

    public UpdateAttachmentsJson getUpdateAttachments()
    {
        return updateAttachments;
    }

    public void setUpdateAttachments( final UpdateAttachmentsJson updateAttachments )
    {
        this.updateAttachments = updateAttachments;
    }

    public ThumbnailJson getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail( final ThumbnailJson thumbnail )
    {
        this.thumbnail = thumbnail;
    }
}
