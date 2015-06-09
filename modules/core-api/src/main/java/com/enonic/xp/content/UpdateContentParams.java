package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.security.PrincipalKey;

@Beta
public final class UpdateContentParams
{
    private ContentId contentId;

    private ContentEditor editor;

    private PrincipalKey modifier;

    private CreateAttachments createAttachments = null;

    private boolean requireValid;

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

    public UpdateContentParams requireValid( final boolean value )
    {
        this.requireValid = value;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( contentId, "contentId cannot be null" );
        Preconditions.checkArgument( editor != null || createAttachments != null, "editor and createAttachments cannot be both null" );
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

    public boolean isRequireValid()
    {
        return requireValid;
    }
}
