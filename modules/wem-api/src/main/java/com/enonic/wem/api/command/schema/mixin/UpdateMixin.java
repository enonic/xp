package com.enonic.wem.api.command.schema.mixin;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.editor.MixinEditor;

public final class UpdateMixin
    extends Command<UpdateMixinResult>
{
    private MixinName name;

    private MixinEditor editor;


    public UpdateMixin name( final MixinName value )
    {
        this.name = value;
        return this;
    }

    public UpdateMixin editor( final MixinEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public MixinName getName()
    {
        return name;
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

        if ( !( o instanceof UpdateMixin ) )
        {
            return false;
        }

        final UpdateMixin that = (UpdateMixin) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
