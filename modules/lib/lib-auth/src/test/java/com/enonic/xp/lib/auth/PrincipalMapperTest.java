package com.enonic.xp.lib.auth;

import java.net.URL;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;

public class PrincipalMapperTest
{

    private final ObjectMapper mapper;

    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    public PrincipalMapperTest()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    @Test
    public void testUserSerialized()
        throws Exception
    {

        final Principal user = User.create().
            key( PrincipalKey.ofUser( UserStoreKey.from( "enonic" ), "user1" ) ).
            displayName( "User 1" ).
            modifiedTime( Instant.now( clock ) ).
            email( "user1@enonic.com" ).
            login( "user1" ).
            build();

        final PrincipalMapper principalMapper = new PrincipalMapper( user );
        assertJson( "user", principalMapper );
    }

    @Test
    public void testGroupSerialized()
        throws Exception
    {

        final Group group = Group.create().
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            displayName( "Group A" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final PrincipalMapper principalMapper = new PrincipalMapper( group );
        assertJson( "group", principalMapper );
    }

    @Test
    public void testRoleSerialized()
        throws Exception
    {

        final Role role = Role.create().
            key( PrincipalKey.ofRole( "aRole" ) ).
            displayName( "Role Display Name" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final PrincipalMapper principalMapper = new PrincipalMapper( role );
        assertJson( "role", principalMapper );
    }

    private void assertJson( final String name, final MapSerializable value )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = this.mapper.readTree( url );

        final JsonMapGenerator generator = new JsonMapGenerator();
        value.serialize( generator );
        final JsonNode actualJson = (JsonNode) generator.getRoot();

        final String expectedStr = this.mapper.writeValueAsString( expectedJson );
        final String actualStr = this.mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }
}
