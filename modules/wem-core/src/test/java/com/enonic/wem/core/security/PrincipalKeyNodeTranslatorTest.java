package com.enonic.wem.core.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeName;

import static org.junit.Assert.*;

public class PrincipalKeyNodeTranslatorTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void toNodeName()
        throws Exception
    {
        final PrincipalKey principalKey = PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "rmy" );

        final User user = User.create().
            key( principalKey ).
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final String nodeName = PrincipalKeyNodeTranslator.toNodeName( user.getKey() ).toString();

        assertEquals( "rmy", nodeName );
    }

    @Test
    public void userKeyFromNode()
        throws Exception
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, Value.newString( PrincipalType.USER ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.USER_STORE_KEY, Value.newString( UserStoreKey.system() ) );

        final Node userNode = Node.newNode().rootDataSet( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        final PrincipalKey principalKey = PrincipalKeyNodeTranslator.toKey( userNode );

        assertTrue( principalKey.isUser() );
        assertEquals( PrincipalType.USER, principalKey.getType() );
        assertEquals( UserStoreKey.system(), principalKey.getUserStore() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknown_type()
        throws Exception
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, Value.newString( "fisk" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.USER_STORE_KEY, Value.newString( UserStoreKey.system() ) );

        final Node userNode = Node.newNode().rootDataSet( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        PrincipalKeyNodeTranslator.toKey( userNode );
    }

}
