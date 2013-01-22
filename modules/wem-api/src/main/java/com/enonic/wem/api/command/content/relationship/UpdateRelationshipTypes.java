package com.enonic.wem.api.command.content.relationship;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.content.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;

public final class UpdateRelationshipTypes
    extends Command<Integer>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    private RelationshipTypeEditor editor;


    public UpdateRelationshipTypes qualifiedNames( final QualifiedRelationshipTypeNames relationshipTypeNames )
    {
        this.qualifiedNames = relationshipTypeNames;
        return this;
    }

    public UpdateRelationshipTypes editor( final RelationshipTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return qualifiedNames;
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

        if ( !( o instanceof UpdateRelationshipTypes ) )
        {
            return false;
        }

        final UpdateRelationshipTypes that = (UpdateRelationshipTypes) o;
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
        Preconditions.checkNotNull( this.qualifiedNames, "Relationship type names cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
