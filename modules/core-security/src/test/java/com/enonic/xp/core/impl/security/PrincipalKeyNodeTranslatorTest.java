package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.xp.core.impl.security.PrincipalKeyNodeTranslator;
import com.enonic.xp.core.impl.security.PrincipalPropertyNames;

import static org.junit.Assert.*;

public class PrincipalKeyNodeTranslatorTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void toNodeName()
        throws Exception
    {
        PrincipalKey principalKey = PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "rmy" );

        User user = User.create().
            key( principalKey ).
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        String nodeName = PrincipalKeyNodeTranslator.toNodeName( user.getKey() ).toString();

        assertEquals( "rmy", nodeName );
    }

    @Test
    public void userKeyFromNode()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, PrincipalType.USER.toString() );
        rootDataSet.setString( PrincipalPropertyNames.USER_STORE_KEY, UserStoreKey.system().toString() );

        Node userNode = Node.newNode().data( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        PrincipalKey principalKey = PrincipalKeyNodeTranslator.toKey( userNode );

        assertTrue( principalKey.isUser() );
        assertEquals( PrincipalType.USER, principalKey.getType() );
        assertEquals( UserStoreKey.system(), principalKey.getUserStore() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_type()
        throws Exception
    {
        PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, "fisk" );
        rootDataSet.setString( PrincipalPropertyNames.USER_STORE_KEY, UserStoreKey.system().toString() );

        Node userNode = Node.newNode().data( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        PrincipalKeyNodeTranslator.toKey( userNode );
    }

}
