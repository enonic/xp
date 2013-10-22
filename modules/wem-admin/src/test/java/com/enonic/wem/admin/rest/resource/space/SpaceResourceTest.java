package com.enonic.wem.admin.rest.resource.space;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.UniformInterfaceException;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;

import static com.enonic.wem.api.command.Commands.space;
import static org.junit.Assert.*;


public class SpaceResourceTest
    extends AbstractResourceTest
{

    private Client client;

    private UploadService uploadService;

    private final String currentTime = "2013-08-23T12:55:09.162Z";

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

    private Space createSpace( String name )
    {
        return Space.newSpace().name( name ).displayName( name ).rootContent( ContentId.from( "root" ) ).createdTime(
            DateTime.parse( currentTime ) ).modifiedTime( DateTime.parse( currentTime ) ).build();
    }

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void testSpaceDeleteExistedSpaces()
        throws Exception
    {
        resource().path( "space/delete" ).entity( readFromFile( "existed_space_names.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
    }

    @Test
    public void testSpaceDeleteNonExistedSpaces()
        throws Exception
    {
        try
        {
            resource().path( "space/delete" ).entity( readFromFile( "non_existed_space_names.json" ),
                                                      MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Space [Space4, Space5, Space6] not found" );
        }
    }


    @Test
    public void testSpaceDeleteExistedAndNonExistedSpaces()
        throws Exception
    {
        try
        {
            resource().path( "space/delete" ).entity( readFromFile( "existed_and_non_existed_space_names.json" ),
                                                      MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Space [Space4, Space5, Space6] not found" );
        }
    }


    @Test
    public void testSpaceCreateNewSpace()
        throws Exception
    {
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
        resource().path( "space/create" ).entity( readFromFile( "new_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
    }

    @Test
    public void testSpaceCreateNewSpaceWithExistingName()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn(
                Spaces.from( Space.newSpace().name( "space-1" ).build() ) );
            resource().path( "space/create" ).entity( readFromFile( "new_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 406 );
            assertEquals( e.getResponse().getEntity( String.class ), "Space with name space-1 already exists" );
        }
    }

    @Test
    public void testSpaceCreateNewSpaceWithWrongIcon()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
            UploadItem item = Mockito.mock( UploadItem.class );
            Mockito.when( item.getMimeType() ).thenReturn( "WrongType" );
            Mockito.when( uploadService.getItem( Mockito.anyString() ) ).thenReturn( item );
            resource().path( "space/create" ).entity( readFromFile( "new_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 500 );
        }
    }

    @Test
    public void testSpaceUpdateExistedSpace()
        throws Exception
    {
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn(
            Spaces.from( Space.newSpace().name( "space-1" ).build() ) );
        resource().path( "space/update" ).entity( readFromFile( "update_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
    }

    @Test
    public void testSpaceUpdateNonExistedSpace()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
            resource().path( "space/update" ).entity( readFromFile( "update_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
            assertEquals( e.getResponse().getEntity( String.class ), "Space space-1 not found" );
        }

    }

    @Test
    public void testSpaceUpdateSpaceWithWrongIcon()
        throws Exception
    {
        try
        {
            Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
            UploadItem item = Mockito.mock( UploadItem.class );
            Mockito.when( item.getMimeType() ).thenReturn( "WrongType" );
            Mockito.when( uploadService.getItem( Mockito.anyString() ) ).thenReturn( item );
            resource().path( "space/update" ).entity( readFromFile( "update_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 500 );
        }
    }

    @Test
    public void testSpaceUpdateExistedSpaceNoRename()
        throws Exception
    {
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn(
            Spaces.from( Space.newSpace().name( "space-1" ).build() ) );
        resource().path( "space/update" ).entity( readFromFile( "new_space.json" ), MediaType.APPLICATION_JSON_TYPE ).post();
    }

    @Test
    public void testSpaceList()
        throws Exception
    {
        Mockito.when( client.execute( space().get().all() ) ).thenReturn(
            Spaces.from( createSpace( "space-1" ), createSpace( "space-2" ), createSpace( "space-3" ) ) );
        String json = resource().path( "space/list" ).get( String.class );
        assertJson( "space_list.json", json );
    }

    @Test
    public void testSpaceDetails()
        throws Exception
    {
        Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn(
            Spaces.from( createSpace( "space-1" ) ) );
        String json = resource().path( "space" ).queryParam( "name", "space-1" ).get( String.class );
        assertJson( "space_details.json", json );
    }

    @Test
    public void testNonExistedSpaceDetails()
    {
        try
        {
            Mockito.when( client.execute( space().get().name( SpaceName.from( "space-1" ) ) ) ).thenReturn( Spaces.empty() );
            resource().path( "space" ).queryParam( "name", "space-1" ).get( String.class );
        }
        catch ( UniformInterfaceException e )
        {
            assertEquals( e.getResponse().getStatus(), 404 );
        }

    }

    @Override
    protected Object getResourceInstance()
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
        final SpaceResource resource = new SpaceResource();
        resource.setClient( client );
        resource.setUploadService( uploadService );
        return resource;
    }
}
