package com.enonic.wem.api.content;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.attachment.CreateAttachments;
import com.enonic.wem.api.security.PrincipalKey;

public final class UpdateContentParams
{
    private ContentId contentId;

    private ContentEditor editor;

    private PrincipalKey modifier;

    private CreateAttachments createAttachments = null;

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

    public UpdateContentParams modifier( final PrincipalKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    public UpdateContentParams createAttachments( final CreateAttachments value )
    {
        this.createAttachments = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( contentId, "contentId cannot be null" );
        Preconditions.checkNotNull( editor, "editor cannot be null" );
    }

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }


    public ContentId getContentId()
    {
        return contentId;
    }

    public CreateAttachments getCreateAttachments()
    {
        return createAttachments;
    }
}
