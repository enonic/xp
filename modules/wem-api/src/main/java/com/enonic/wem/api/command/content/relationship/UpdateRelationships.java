package com.enonic.wem.api.command.content.relationship;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.api.content.relationship.RelationshipKeys;
import com.enonic.wem.api.content.relationship.editor.RelationshipEditor;

public class UpdateRelationships
    extends Command<UpdateRelationshipsResult>
{
    private RelationshipKeys relationshipKeys;

    private RelationshipEditor editor;

    public UpdateRelationships relationshipKeys( final RelationshipKeys value )
    {
        this.relationshipKeys = value;
        return this;
    }

    public RelationshipKeys getRelationshipKeys()
    {
        return relationshipKeys;
    }

    public void editor( final RelationshipEditor relationshipEditor )
    {
        this.editor = relationshipEditor;
    }

    public RelationshipEditor getEditor()
    {
        return editor;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( relationshipKeys, "relationshipKeys cannot be null" );
        Preconditions.checkArgument( relationshipKeys.isNotEmpty(), "relationshipKeys cannot be empty" );

        for ( RelationshipKey relationshipKey : relationshipKeys )
        {
            Preconditions.checkArgument( relationshipKey.getManagingData() != null, "Managed Relationship's cannot be updated directly" );
        }
    }
}
