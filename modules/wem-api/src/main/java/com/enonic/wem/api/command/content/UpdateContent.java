package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.UpdateAttachmentsParams;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContent
    extends Command<Content>
{

    private UpdateAttachmentsParams updateAttachments;

    private ContentEditor editor;

    private UserKey modifier;

    private ContentId contentId;

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UpdateContent editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UpdateContent contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public UpdateContent modifier( final UserKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    public UpdateContent updateAttachments( final UpdateAttachmentsParams updateAttachments )
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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
