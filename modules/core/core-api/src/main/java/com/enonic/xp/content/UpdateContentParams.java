package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.BinaryReferences;

@PublicApi
public final class UpdateContentParams
{
    private ContentId contentId;

    private ContentEditor editor;

    private PrincipalKey modifier;

    private CreateAttachments createAttachments = CreateAttachments.empty();

    private BinaryReferences removeAttachments = BinaryReferences.empty();

    private boolean clearAttachments;

    private boolean requireValid;

    private boolean stopInherit = true;

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

    public UpdateContentParams createAttachments( final CreateAttachments value )
    {
        this.createAttachments = Objects.requireNonNullElseGet( value, CreateAttachments::empty );
        return this;
    }

    public UpdateContentParams requireValid( final boolean value )
    {
        this.requireValid = value;
        return this;
    }

    public UpdateContentParams removeAttachments( final BinaryReferences removeAttachments )
    {
        this.removeAttachments = Objects.requireNonNullElseGet( removeAttachments, BinaryReferences::empty );
        return this;
    }

    public UpdateContentParams clearAttachments( final boolean clearAttachments )
    {
        this.clearAttachments = clearAttachments;
        return this;
    }

    public UpdateContentParams stopInherit( final boolean stopInherit )
    {
        this.stopInherit = stopInherit;
        return this;
    }

    public void validate()
    {
        Preconditions.checkNotNull( contentId, "contentId cannot be null" );
    }

    public ContentEditor getEditor()
    {
        return this.editor;
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

    public BinaryReferences getRemoveAttachments()
    {
        return removeAttachments;
    }

    public boolean isClearAttachments()
    {
        return clearAttachments;
    }

    public boolean stopInherit()
    {
        return stopInherit;
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
