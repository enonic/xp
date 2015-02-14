package com.enonic.xp.core.impl.relationship;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.relationship.CreateRelationshipParams;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.relationship.RelationshipNotFoundException;
import com.enonic.xp.relationship.RelationshipService;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.relationship.UpdateRelationshipParams;

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
