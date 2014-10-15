package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public final class ModuleRelationshipTypeProvider
    implements RelationshipTypeProvider
{
    private final Module module;

    public ModuleRelationshipTypeProvider( final Module module )
    {
        this.module = module;
    }

    @Override
    public RelationshipTypes get()
    {
        return new RelationshipTypeLoader().loadRelationshipTypes( module );
    }

}
