package com.enonic.wem.admin.rest.resource.space;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;

import static com.enonic.wem.api.command.Commands.space;


public class SpaceResourceTest
{

    private Client client;

    @Before
    public void setup()
    {
        client = Mockito.mock( Client.class );

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
        final SpaceResource spaceResource = new SpaceResource();
        spaceResource.setClient( client );
        List<String> spaceNames = createExistedSpaceNames();
        spaceNames.addAll( createNonExistedSpaceNames() );
        spaceResource.delete( spaceNames );
    }
}
