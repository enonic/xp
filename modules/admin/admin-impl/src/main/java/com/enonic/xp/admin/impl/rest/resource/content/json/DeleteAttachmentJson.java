package com.enonic.xp.admin.impl.rest.resource.content.json;


import com.enonic.xp.content.ContentId;
import com.enonic.xp.util.BinaryReferences;

public class DeleteAttachmentJson
{
    private ContentId contentId;

    private BinaryReferences attachmentNames;

    public ContentId getContentId()
    {
        return contentId;
    }

    public void setContentId( final String contentId )
    {
        this.contentId = contentId != null ? ContentId.from( contentId ) : null;
    }

    public BinaryReferences getAttachmentReferences()
    {
        return attachmentNames;
    }

    public void setAttachmentNames( final String[] attachmentNames )
    {
        this.attachmentNames = BinaryReferences.from( attachmentNames );
    }
}
