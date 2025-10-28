package com.enonic.xp.core.impl.security;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrincipalKeyNodeTranslatorTest
{
    @Test
    void toNodeName()
    {
        PrincipalKey principalKey = PrincipalKey.ofUser( IdProviderKey.from( "myidprovider" ), "rmy" );

        User user = User.create().
            key( principalKey ).
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            build();

        String nodeName = PrincipalKeyNodeTranslator.toNodeName( user.getKey() ).toString();

        assertEquals( "rmy", nodeName );
    }

    @Test
    void userKeyFromNode()
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, PrincipalType.USER.toString() );
        rootDataSet.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, IdProviderKey.system().toString() );

        Node userNode = Node.create().data( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        PrincipalKey principalKey = PrincipalKeyNodeTranslator.toKey( userNode );

        assertTrue( principalKey.isUser() );
        assertEquals( PrincipalType.USER, principalKey.getType() );
        assertEquals( IdProviderKey.system(), principalKey.getIdProviderKey() );
    }

    @Test
    void unknown_type()
    {
        PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, "fisk" );
        rootDataSet.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, IdProviderKey.system().toString() );

        Node userNode = Node.create().data( rootDataSet ).
            name( NodeName.from( "rmy" ) ).
            build();

        assertThrows(IllegalArgumentException.class, () -> PrincipalKeyNodeTranslator.toKey( userNode ));
    }

}
