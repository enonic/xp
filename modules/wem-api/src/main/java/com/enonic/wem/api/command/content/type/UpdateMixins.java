package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.editor.MixinEditor;

public final class UpdateMixins
    extends Command<Integer>
{
    private QualifiedMixinNames qualifiedMixinNames;

    private MixinEditor editor;


    public UpdateMixins names( final QualifiedMixinNames qualifiedMixinNames )
    {
        this.qualifiedMixinNames = qualifiedMixinNames;
        return this;
    }

    public UpdateMixins editor( final MixinEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedMixinNames getQualifiedMixinNames()
    {
        return qualifiedMixinNames;
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
        return Objects.equal( this.qualifiedMixinNames, that.qualifiedMixinNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedMixinNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedMixinNames, "Content type names cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
