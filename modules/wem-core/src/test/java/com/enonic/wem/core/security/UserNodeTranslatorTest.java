package com.enonic.wem.core.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;

import static org.junit.Assert.*;

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
            key( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );

        assertEquals( "rmy", createNodeParams.getName() );

        final PropertyTree rootDataSet = createNodeParams.getData();
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getString( PrincipalNodeTranslator.USER_STORE_KEY ) );
        assertEquals( PrincipalType.USER.toString(), rootDataSet.getString( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY ) );
        assertEquals( "displayname", rootDataSet.getString( PrincipalNodeTranslator.DISPLAY_NAME_KEY ) );
        assertNotNull( rootDataSet );
        assertEquals( 5, rootDataSet.getTotalSize() );
    }


    @Test
    public void toUser()
        throws Exception
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( UserStoreKey.system(), "i-am-a-user" );

        final PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setString( PrincipalNodeTranslator.LOGIN_KEY, "loginkey" );
        rootDataSet.setString( PrincipalNodeTranslator.EMAIL_KEY, "rmy@enonic.com" );
        rootDataSet.setString( PrincipalNodeTranslator.DISPLAY_NAME_KEY, "displayname" );
        rootDataSet.setString( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, userKey.getType().toString() );
        rootDataSet.setString( PrincipalNodeTranslator.USER_STORE_KEY, userKey.getUserStore().toString() );

        final Node node = Node.newNode().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( userKey ) ).
            data( rootDataSet ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final User user = (User) PrincipalNodeTranslator.fromNode( node );
        assertEquals( "loginkey", user.getLogin() );
        assertEquals( "rmy@enonic.com", user.getEmail() );
        assertEquals( userKey, user.getKey() );
    }
}