package com.enonic.wem.api.command.content.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.schema.mixin.editor.MixinEditor;

public final class UpdateMixins
    extends Command<Integer>
{
    private QualifiedMixinNames qualifiedNames;

    private MixinEditor editor;


    public UpdateMixins qualifiedNames( final QualifiedMixinNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
        return this;
    }

    public UpdateMixins editor( final MixinEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedMixinNames getQualifiedNames()
    {
        return qualifiedNames;
    }

    public MixinEditor getEditor()
    {
        return editor;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof UpdateMixins ) )
        {
            return false;
        }

        final UpdateMixins that = (UpdateMixins) o;
        return Objects.equal( this.qualifiedNames, that.qualifiedNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedNames, "qualifiedNames cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
