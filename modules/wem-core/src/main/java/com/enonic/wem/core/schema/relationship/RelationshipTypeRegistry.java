package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public interface RelationshipTypeRegistry
{

    RelationshipType getRelationshipType( final RelationshipTypeName name );

    RelationshipTypes getRelationshipTypeByModule( final ModuleKey moduleKey );

    RelationshipTypes getAllRelationshipTypes();

}
