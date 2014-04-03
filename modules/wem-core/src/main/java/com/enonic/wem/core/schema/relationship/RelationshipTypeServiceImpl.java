package com.enonic.wem.core.schema.relationship;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.relationship.CreateRelationshipTypeParams;
import com.enonic.wem.api.command.schema.relationship.DeleteRelationshipTypeResult;
import com.enonic.wem.api.command.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeService;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeParams;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipTypeResult;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;

public class RelationshipTypeServiceImpl
    implements RelationshipTypeService
{
    @Inject
    protected RelationshipTypeDao relationshipTypeDao;

    @Override
    public RelationshipTypes getAll()
    {
        return relationshipTypeDao.getAllRelationshipTypes();
    }

    @Override
    public RelationshipType getByName( final GetRelationshipTypeParams params )
    {
        return new GetRelationshipTypeCommand().params( params ).relationshipTypeDao( relationshipTypeDao ).execute();
    }

    @Override
    public RelationshipTypesExistsResult exists( final RelationshipTypeNames names )
    {
        return new RelationshipTypesExistsCommand().names( names ).relationshipTypeDao( relationshipTypeDao ).execute();
    }

    @Override
    public RelationshipTypeName create( final CreateRelationshipTypeParams params )
    {
        return new CreateRelationshipTypeCommand().params( params ).relationshipTypeDao( relationshipTypeDao ).execute();
    }

    @Override
    public UpdateRelationshipTypeResult update( final UpdateRelationshipTypeParams params )
    {
        return new UpdateRelationshipTypeCommand().params( params ).relationshipTypeDao( relationshipTypeDao ).execute();
    }

    @Override
    public DeleteRelationshipTypeResult delete( final RelationshipTypeName name )
    {
        return new DeleteRelationshipTypeCommand().name( name ).relationshipTypeDao( relationshipTypeDao ).execute();
    }
}
