package com.enonic.wem.api.command.content.schema.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationship.editor.RelationshipTypeEditor;

public final class UpdateRelationshipType
    extends Command<Boolean>
{
    private QualifiedRelationshipTypeName qualifiedName;

    private RelationshipTypeEditor editor;


    public UpdateRelationshipType selector( final QualifiedRelationshipTypeName qualifiedName )
    {
        this.qualifiedName = qualifiedName;
        return this;
    }

    public UpdateRelationshipType editor( final RelationshipTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedRelationshipTypeName getQualifiedName()
    {
        return qualifiedName;
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

        if ( !( o instanceof UpdateRelationshipType ) )
        {
            return false;
        }

        final UpdateRelationshipType that = (UpdateRelationshipType) o;
        return Objects.equal( this.qualifiedName, that.qualifiedName ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.qualifiedName, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.qualifiedName, "qualifiedName cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
