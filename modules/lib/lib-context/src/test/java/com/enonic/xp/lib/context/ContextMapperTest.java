package com.enonic.xp.lib.context;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextMapperTest
{
    @Test
    void test()
    {
        User user = User.create().
            login( PrincipalKey.ofSuperUser().getId() ).
            displayName( "Super User" ).
            key( PrincipalKey.ofSuperUser() ).
            build();
        AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( user ).
            principals( RoleKeys.ADMIN, RoleKeys.EVERYONE ).
            build();

        Context context = ContextBuilder.create()
            .repositoryId( RepositoryId.from( "repository.id" ) )
            .branch( Branch.create().value( "master" ).build() )
            .authInfo( authInfo )
            .attribute( "attrAsString", "value" )
            .attribute( "attrAsInteger", Integer.MAX_VALUE )
            .attribute( "attrAsLong", Long.MIN_VALUE )
            .attribute( "attrAsBoolean", true )
            .attribute( "authInfoDetails", authInfo )
            .attribute( "testMapper", new TestMapper() )
            .build();

        context.getLocalScope().setAttribute( "attrAsString", "localValue" );
        context.getLocalScope().setAttribute( "attr1", "localValue" );
        context.getLocalScope().setSession( new SessionMock() );
        context.getLocalScope().getSession().setAttribute( "attrAsString", "sessionValue" );
        context.getLocalScope().getSession().setAttribute( "attr2", "sessionValue" );

        JsonMapGenerator generator = new JsonMapGenerator();
        new ContextMapper( context ).serialize( generator );

        JsonNode actualJson = (JsonNode) generator.getRoot();

        JsonNode attributes = actualJson.get( "attributes" );

        assertNull( attributes.get( "authInfoDetails" ) );
        assertNull( attributes.get( Branch.class.getName() ) );
        assertNull( attributes.get( RepositoryId.class.getName() ) );
        assertNull( attributes.get( AuthenticationInfo.class.getName() ) );

        assertEquals( "value", attributes.get( "attrAsString" ).asText() );
        assertEquals( Integer.MAX_VALUE, attributes.get( "attrAsInteger" ).asInt() );
        assertTrue( attributes.get( "attrAsBoolean" ).asBoolean() );
        assertEquals( Long.MIN_VALUE, attributes.get( "attrAsLong" ).asLong() );
        assertNotNull( attributes.get( "testMapper" ) );
        assertEquals( "localValue", attributes.get( "attr1" ).asText() );
        assertEquals( "sessionValue", attributes.get( "attr2" ).asText() );
    }

    public static final class TestMapper
        implements MapSerializable
    {
        @Override
        public void serialize( final MapGenerator gen )
        {
            gen.value( "key", "value" );
        }
    }
}
