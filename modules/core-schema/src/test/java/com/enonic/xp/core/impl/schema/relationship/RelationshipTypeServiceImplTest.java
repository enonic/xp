package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;
import com.enonic.xp.module.Module;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

import static org.junit.Assert.*;

public class RelationshipTypeServiceImplTest
    extends AbstractBundleTest
{

    private static int DEFAULT_RELATIONSHIP_TYPES_NUMBER = 2;

    private Bundle myBundle;

    private ApplicationKey myApplicationKey;

    private RelationshipType myModuleType;

    private Module myModule;

    private ApplicationService applicationService;

    private RelationshipTypeServiceImpl relationshipTypeService;

    @Before
    @Override
    public void setup()
        throws Exception
    {
        super.setup();

        //Mocks a module
        startBundles( newBundle( "module2" ) );
        myBundle = findBundle( "module2" );
        myApplicationKey = ApplicationKey.from( myBundle );
        myModuleType = createType( myApplicationKey + ":member" );
        myModule = Mockito.mock( Module.class );
        Mockito.when( myModule.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myModule.getBundle() ).thenReturn( myBundle );

        //Mocks the module service
        applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getAllModules() ).thenReturn( Applications.empty() );

        //Mocks the ComponentContext
        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( this.serviceRegistry.getBundleContext() );

        //Creates the service to test
        relationshipTypeService = new RelationshipTypeServiceImpl();
        relationshipTypeService.setApplicationService( applicationService );

        //Starts the service
        relationshipTypeService.start( componentContext );
    }

    @Test
    public void test_empty()
    {
        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( myApplicationKey );
        assertNotNull( relationshipTypes );
        assertEquals( 0, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myModuleType.getName() );
        assertEquals( null, relationshipType );
    }

    @Test
    public void test_add_removal_module()
    {
        Applications applications = Applications.from( myModule );
        Mockito.when( applicationService.getAllModules() ).thenReturn( applications );
        Mockito.when( applicationService.getModule( myApplicationKey ) ).thenReturn( myModule );

        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER + 1, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( myApplicationKey );
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myModuleType.getName() );
        assertNotNull( relationshipType );

        Mockito.when( applicationService.getAllModules() ).thenReturn( Applications.empty() );
        Mockito.when( applicationService.getModule( myApplicationKey ) ).thenReturn( null );
        relationshipTypeService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_module()
    {
        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByModule( ApplicationKey.SYSTEM );
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( RelationshipTypeName.PARENT );
        assertNotNull( relationshipType );

        relationshipType = relationshipTypeService.getByName( RelationshipTypeName.REFERENCE );
        assertNotNull( relationshipType );
    }

    @Test
    public void test_stop()
    {
        relationshipTypeService.stop();
    }

    private RelationshipType createType( final String name )
    {
        return RelationshipType.create().name( name ).build();
    }
}
