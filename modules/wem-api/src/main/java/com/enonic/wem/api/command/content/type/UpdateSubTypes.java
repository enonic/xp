package com.enonic.wem.api.command.content.type;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.editor.SubTypeEditor;

public final class UpdateSubTypes
    extends Command<Integer>
{
    private QualifiedSubTypeNames subTypeNames;

    private SubTypeEditor editor;


    public UpdateSubTypes names( final QualifiedSubTypeNames qualifiedSubTypeNames )
    {
        this.subTypeNames = qualifiedSubTypeNames;
        return this;
    }

    public UpdateSubTypes editor( final SubTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedSubTypeNames getNames()
    {
        return subTypeNames;
    }

    public SubTypeEditor getEditor()
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

        if ( !( o instanceof UpdateSubTypes ) )
        {
            return false;
        }

        final UpdateSubTypes that = (UpdateSubTypes) o;
        return Objects.equal( this.subTypeNames, that.subTypeNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.subTypeNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.subTypeNames, "Content type names cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
