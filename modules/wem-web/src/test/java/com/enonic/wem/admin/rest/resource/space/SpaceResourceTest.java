package com.enonic.wem.admin.rest.resource.space;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.space.exception.DuplicatedSpaceException;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;

import static com.enonic.wem.api.command.Commands.space;


public class SpaceResourceTest
{

    private Client client;

    private UploadService uploadService;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );
        uploadService = Mockito.mock( UploadService.class );

        for ( SpaceName spaceName : SpaceNames.from( createExistedSpaceNames().toArray( new String[0] ) ) )
        {
            Mockito.when( client.execute( space().delete().name( spaceName ) ) ).thenReturn( true );
        }

        for ( SpaceName spaceName : SpaceNames.from( createNonExistedSpaceNames().toArray( new String[0] ) ) )
        {
            Mockito.when( client.execute( space().delete().name( spaceName ) ) ).thenReturn( false );
        }
    }

    private List<String> createExistedSpaceNames()
    {
        List<String> spaceNames = new ArrayList<>();
        spaceNames.add( "Space1" );
        spaceNames.add( "Space2" );
        spaceNames.add( "Space3" );
        return spaceNames;
    }

    private List<String> createNonExistedSpaceNames()
    {
        List<String> spaceNames = new ArrayList<>();
        spaceNames.add( "Space4" );
        spaceNames.add( "Space5" );
        spaceNames.add( "Space6" );
        return spaceNames;
    }

    private SpaceResource createSpaceResource()
    {
        final SpaceResource spaceResource = new SpaceResource();
        spaceResource.setClient( client );
        spaceResource.setUploadService( uploadService );
        return spaceResource;
    }

    @Test
    public void testSpaceDeleteExistedSpaces()
        throws Exception
    {
        final SpaceResource spaceResource = new SpaceResource();
        spaceResource.setClient( client );

        spaceResource.delete( createExistedSpaceNames() );
    }

    @Test(expected = NotFoundException.class)
    public void testSpaceDeleteNonExistedSpaces()
    {
        final SpaceResource spaceResource = new SpaceResource();
        spaceResource.setClient( client );

        spaceResource.delete( createNonExistedSpaceNames() );
    }


    @Test(expected = NotFoundException.class)
    public void testSpaceDeleteExistedAndNonExistedSpaces()
    {
        final SpaceResource spaceResource = createSpaceResource();
        List<String> spaceNames = createExistedSpaceNames();
        spaceNames.addAll( createNonExistedSpaceNames() );
        spaceResource.delete( spaceNames );
    }


    @Test
    public void testSpaceCreateNewSpace()
    {
        final SpaceResource spaceResource = createSpaceResource();
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
        spaceResource.create( "space-1", "Space 1", "icon.png" );
    }

    @Test(expected = DuplicatedSpaceException.class)
    public void testSpaceCreateNewSpaceWithExistingName()
    {
        final SpaceResource spaceResource = createSpaceResource();
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn(
            Spaces.from( Space.newSpace().name( "space-1" ).build() ) );
        spaceResource.create( "space-1", "Space 1", "icon.png" );
    }

}
