package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class RelationshipTypeServiceImplTest
    extends AbstractBundleTest
{

    private static int DEFAULT_RELATIONSHIP_TYPES_NUMBER = 2;

    private Bundle myBundle;

    private ModuleKey myModuleKey;

    private RelationshipType myModuleType;

    private Module myModule;

    private ModuleService moduleService;

    private RelationshipTypeServiceImpl relationshipTypeService;

    @Before
    @Override
    public void setup()
        throws Exception
    {
        super.setup();

        //Mocks two modules
        startBundles( newBundle( "module2" ) );
        myBundle = findBundle( "module2" );
        myModuleKey = ModuleKey.from( myBundle );
        myModuleType = createType( myModuleKey + ":member" );
        myModule = Mockito.mock( Module.class );
        Mockito.when( myModule.getKey() ).thenReturn( myModuleKey );
        Mockito.when( myModule.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        moduleService = Mockito.mock( ModuleService.class );
        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );

        //Creates the service to test
        relationshipTypeService = new RelationshipTypeServiceImpl();
        relationshipTypeService.setModuleService( moduleService );
    }

    @Test
    public void test_empty()
    {
        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( myModuleKey );
        assertNotNull( relationshipTypes );
        assertEquals( 0, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myModuleType.getName() );
        assertEquals( null, relationshipType );
    }

    @Test
    public void test_add_removal_module()
    {
        Modules modules = Modules.from( myModule );
        Mockito.when( moduleService.getAllModules() ).thenReturn( modules );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( myModule );

        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER + 1, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( myModuleKey );
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myModuleType.getName() );
        assertNotNull( relationshipType );

        Mockito.when( moduleService.getAllModules() ).thenReturn( Modules.empty() );
        Mockito.when( moduleService.getModule( myModuleKey ) ).thenReturn( null );
        relationshipTypeService.bundleChanged( new BundleEvent( BundleEvent.STOPPED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {
        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( ModuleKey.SYSTEM );
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( RelationshipTypeName.PARENT );
        assertNotNull( relationshipType );

        relationshipType = relationshipTypeService.getByName( RelationshipTypeName.REFERENCE );
        assertNotNull( relationshipType );
    }

    private RelationshipType createType( final String name )
    {
        return RelationshipType.newRelationshipType().name( name ).build();
    }
}
