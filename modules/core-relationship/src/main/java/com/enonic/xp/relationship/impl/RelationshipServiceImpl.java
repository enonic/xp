package com.enonic.xp.relationship.impl;

import org.osgi.service.component.annotations.Component;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.CreateRelationshipParams;
import com.enonic.wem.api.relationship.RelationshipId;
import com.enonic.wem.api.relationship.RelationshipNotFoundException;
import com.enonic.wem.api.relationship.RelationshipService;
import com.enonic.wem.api.relationship.Relationships;
import com.enonic.wem.api.relationship.UpdateRelationshipParams;

@Component
public final class RelationshipServiceImpl
    implements RelationshipService
{
    @Override
    public Relationships getAll( final ContentId id )
    {
        // TODO: Implement
        return Relationships.empty();
    }

    @Override
    public RelationshipId create( final CreateRelationshipParams params )
    {
        // TODO: Implement
        return null;
    }

    @Override
    public void update( final UpdateRelationshipParams params )
        throws RelationshipNotFoundException
    {
        // TODO: Implement
    }

    @Override
    public void delete( final RelationshipId id )
        throws RelationshipNotFoundException
    {
        // TODO: Implement
    }
}
