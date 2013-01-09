package com.enonic.wem.api.command.content.relation;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.content.relation.editor.RelationshipTypeEditor;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;

public final class UpdateRelationshipTypes
    extends Command<Integer>
{
    private QualifiedRelationshipTypeNames relationshipTypeNames;

    private RelationshipTypeEditor editor;


    public UpdateRelationshipTypes names( final QualifiedRelationshipTypeNames relationshipTypeNames )
    {
        this.relationshipTypeNames = relationshipTypeNames;
        return this;
    }

    public UpdateRelationshipTypes editor( final RelationshipTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public QualifiedRelationshipTypeNames getNames()
    {
        return relationshipTypeNames;
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
        return Objects.equal( this.relationshipTypeNames, that.relationshipTypeNames ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.relationshipTypeNames, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.relationshipTypeNames, "Relationship type names cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
