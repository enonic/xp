package com.enonic.wem.api.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;

public final class UpdateRelationshipTypeParams
{
    private RelationshipTypeName name;

    private RelationshipTypeEditor editor;

    public UpdateRelationshipTypeParams name( final RelationshipTypeName name )
    {
        this.name = name;
        return this;
    }

    public UpdateRelationshipTypeParams editor( final RelationshipTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public RelationshipTypeName getName()
    {
        return name;
    }

    public RelationshipTypeEditor getEditor()
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

        if ( !( o instanceof UpdateRelationshipTypeParams ) )
        {
            return false;
        }

        final UpdateRelationshipTypeParams that = (UpdateRelationshipTypeParams) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.name, this.editor );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
