package com.enonic.xp.core.impl.relationship;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.relationship.CreateRelationshipParams;
import com.enonic.xp.core.relationship.RelationshipId;
import com.enonic.xp.core.relationship.RelationshipNotFoundException;
import com.enonic.xp.core.relationship.RelationshipService;
import com.enonic.xp.core.relationship.Relationships;
import com.enonic.xp.core.relationship.UpdateRelationshipParams;

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
