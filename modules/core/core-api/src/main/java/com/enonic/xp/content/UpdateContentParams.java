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

    @Deprecated
    public UpdateContentParams modifier( final PrincipalKey modifier )
    {
        this.modifier = modifier;
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

    @Deprecated
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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final UpdateContentParams that = (UpdateContentParams) o;
        return clearAttachments == that.clearAttachments && requireValid == that.requireValid &&
            Objects.equals( contentId, that.contentId ) && Objects.equals( editor, that.editor ) &&
            Objects.equals( modifier, that.modifier ) && Objects.equals( createAttachments, that.createAttachments ) &&
            Objects.equals( removeAttachments, that.removeAttachments ) && Objects.equals( stopInherit, that.stopInherit );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentId, editor, modifier, createAttachments, removeAttachments, clearAttachments, requireValid,
                             stopInherit );
    }
}
