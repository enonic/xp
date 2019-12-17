package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserNodeTranslatorTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void toCreateNode()
        throws Exception
    {
        final User user = User.create().
            displayName( "displayname" ).
            email( "rmy@enonic.com" ).
            login( "login" ).
            authenticationHash( "password" ).
            key( PrincipalKey.ofUser( IdProviderKey.system(), "rmy" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );

        assertEquals( "rmy", createNodeParams.getName() );

        final PropertyTree rootDataSet = createNodeParams.getData();
        assertEquals( IdProviderKey.system().toString(), rootDataSet.getString( PrincipalPropertyNames.ID_PROVIDER_KEY ) );
        assertEquals( PrincipalType.USER.toString(), rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) );
        assertEquals( "displayname", rootDataSet.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) );
        assertNotNull( rootDataSet );
        assertEquals( 7, rootDataSet.getTotalSize() );
    }


    @Test
    public void toUser()
        throws Exception
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( IdProviderKey.system(), "i-am-a-user" );

        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( PrincipalPropertyNames.LOGIN_KEY, "loginkey" );
        rootDataSet.setString( PrincipalPropertyNames.EMAIL_KEY, "rmy@enonic.com" );
        rootDataSet.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "displayname" );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, userKey.getType().toString() );
        rootDataSet.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, userKey.getIdProviderKey().toString() );
        rootDataSet.setString( PrincipalPropertyNames.AUTHENTICATION_HASH_KEY, "clear:password" );

        final Node node = Node.create().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( userKey ) ).
            data( rootDataSet ).
            build();

        final User user = (User) PrincipalNodeTranslator.fromNode( node );
        assertEquals( "loginkey", user.getLogin() );
        assertEquals( "rmy@enonic.com", user.getEmail() );
        assertEquals( userKey, user.getKey() );
        assertEquals( "clear:password", user.getAuthenticationHash() );
    }
}
