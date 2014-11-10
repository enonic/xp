package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.UpdateAttachmentsParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.security.PrincipalKey;

public final class UpdateContentParams
{
    private UpdateAttachmentsParams updateAttachments;

    private ContentEditor editor;

    private PrincipalKey modifier;

    private ContentId contentId;

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public UpdateContentParams editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UpdateContentParams contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public UpdateContentParams modifier( final PrincipalKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    public UpdateContentParams updateAttachments( final UpdateAttachmentsParams updateAttachments )
    {
        this.updateAttachments = updateAttachments;
        return this;
    }


    public Attachment getAttachment( final String attachmentName )
    {
        return updateAttachments.getAttachments().getAttachment( attachmentName );
    }

    public UpdateAttachmentsParams getUpdateAttachments()
    {
        return updateAttachments;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
