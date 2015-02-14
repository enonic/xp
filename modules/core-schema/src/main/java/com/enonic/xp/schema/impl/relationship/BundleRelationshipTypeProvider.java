package com.enonic.xp.schema.impl.relationship;

import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public final class BundleRelationshipTypeProvider
    implements RelationshipTypeProvider
{
    private final RelationshipTypes types;

    private BundleRelationshipTypeProvider( final RelationshipTypes types )
    {
        this.types = types;
    }

    @Override
    public RelationshipTypes get()
    {
        return this.types;
    }

    public static BundleRelationshipTypeProvider create( final Bundle bundle )
    {
        final RelationshipTypes types = new RelationshipTypeLoader( bundle ).load();
        return types != null ? new BundleRelationshipTypeProvider( types ) : null;
    }
}
