package com.enonic.xp.core.impl.schema.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.core.impl.schema.AbstractBundleTest;
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

    private RelationshipType myApplicationType;

    private Application myApplication;

    private ApplicationService applicationService;

    private RelationshipTypeServiceImpl relationshipTypeService;

    @Before
    @Override
    public void setup()
        throws Exception
    {
        super.setup();

        //Mocks an application
        startBundles( newBundle( "application2" ) );
        myBundle = findBundle( "application2" );
        myApplicationKey = ApplicationKey.from( myBundle );
        myApplicationType = createType( myApplicationKey + ":member" );
        myApplication = Mockito.mock( Application.class );
        Mockito.when( myApplication.getKey() ).thenReturn( myApplicationKey );
        Mockito.when( myApplication.getBundle() ).thenReturn( myBundle );

        //Mocks the application service
        applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );

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

        relationshipTypes = relationshipTypeService.getByApplication( myApplicationKey );
        assertNotNull( relationshipTypes );
        assertEquals( 0, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myApplicationType.getName() );
        assertEquals( null, relationshipType );
    }

    @Test
    public void test_add_removal_application()
    {
        Applications applications = Applications.from( myApplication );
        Mockito.when( applicationService.getAllApplications() ).thenReturn( applications );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( myApplication );

        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER + 1, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByApplication( myApplicationKey );
        assertNotNull( relationshipTypes );
        assertEquals( 1, relationshipTypes.getSize() );

        RelationshipType relationshipType = relationshipTypeService.getByName( myApplicationType.getName() );
        assertNotNull( relationshipType );

        Mockito.when( applicationService.getAllApplications() ).thenReturn( Applications.empty() );
        Mockito.when( applicationService.getApplication( myApplicationKey ) ).thenReturn( null );
        relationshipTypeService.bundleChanged( new BundleEvent( BundleEvent.UNINSTALLED, myBundle ) );

        test_empty();
    }

    @Test
    public void test_get_system_application()
    {
        RelationshipTypes relationshipTypes = relationshipTypeService.getAll();
        assertNotNull( relationshipTypes );
        assertEquals( DEFAULT_RELATIONSHIP_TYPES_NUMBER, relationshipTypes.getSize() );

        relationshipTypes = relationshipTypeService.getByApplication( ApplicationKey.SYSTEM );
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
