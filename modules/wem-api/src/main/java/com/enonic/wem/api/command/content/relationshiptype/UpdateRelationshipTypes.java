package com.enonic.wem.api.command.content.relationshiptype;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
import com.enonic.wem.api.content.relationshiptype.editor.RelationshipTypeEditor;

public final class UpdateRelationshipTypes
    extends Command<Integer>
{
    private RelationshipTypeSelectors selectors;

    private RelationshipTypeEditor editor;


    public UpdateRelationshipTypes selectors( final RelationshipTypeSelectors selectors )
    {
        this.selectors = selectors;
        return this;
    }

    public UpdateRelationshipTypes editor( final RelationshipTypeEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public RelationshipTypeSelectors getSelectors()
    {
        return selectors;
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
        return Objects.equal( this.selectors, that.selectors ) && Objects.equal( this.editor, that.editor );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( this.selectors, this.editor );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selectors, "selectors cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
