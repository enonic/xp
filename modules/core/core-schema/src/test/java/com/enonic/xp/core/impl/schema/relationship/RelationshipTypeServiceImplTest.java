package com.enonic.xp.core.impl.schema.relationship;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractSchemaTest;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RelationshipTypeServiceImplTest
    extends AbstractSchemaTest
{
    protected RelationshipTypeServiceImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new RelationshipTypeServiceImpl();
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testEmpty()
    {
        final RelationshipTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 2, types1.getSize() );

        final RelationshipTypes types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final RelationshipType mixin = service.getByName( RelationshipTypeName.from( "other:mytype" ) );
        assertEquals( null, mixin );
    }

    @Test
    public void testSystemTypes()
    {
        RelationshipTypes relationshipTypes = service.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );

        relationshipTypes = service.getByApplication( ApplicationKey.SYSTEM );
        assertNotNull( relationshipTypes );
        assertEquals( 2, relationshipTypes.getSize() );

        RelationshipType relationshipType = service.getByName( RelationshipTypeName.PARENT );
        assertNotNull( relationshipType );

        relationshipType = service.getByName( RelationshipTypeName.REFERENCE );
        assertNotNull( relationshipType );
    }

    @Test
    public void testApplications()
    {
        initializeApps();

        final RelationshipTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 3, types1.getSize() );

        final RelationshipTypes types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final RelationshipTypes types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 1, types3.getSize() );

        final RelationshipType type = service.getByName( RelationshipTypeName.from( "myapp2:member" ) );
        assertNotNull( type );
    }
}
