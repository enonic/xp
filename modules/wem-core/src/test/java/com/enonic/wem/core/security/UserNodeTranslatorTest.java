package com.enonic.wem.core.security;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;

import static org.junit.Assert.*;

public class UserNodeTranslatorTest
{
    @Test
    public void toCreateNode()
        throws Exception
    {
        final User user = User.create().
            displayName( "displayname" ).
            email( "rmy@enonic.com" ).
            login( "login" ).
            key( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );

        assertEquals( "rmy", createNodeParams.getName() );

        final RootDataSet rootDataSet = createNodeParams.getData();
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getProperty( PrincipalNodeTranslator.USERSTORE_KEY ).getString() );
        assertEquals( PrincipalType.USER.toString(), rootDataSet.getProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY ).getString() );
        assertEquals( "displayname", rootDataSet.getProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY ).getString() );
        assertNotNull( rootDataSet );
        assertEquals( 5, rootDataSet.size() );
    }


    @Test
    public void toUser()
        throws Exception
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( UserStoreKey.system(), "i-am-a-user" );

        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( PrincipalNodeTranslator.LOGIN_KEY, Value.newString( "loginkey" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.EMAIL_KEY, Value.newString( "rmy@enonic.com" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY, Value.newString( "displayname" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, Value.newString( userKey.getType().toString() ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.USERSTORE_KEY, Value.newString( userKey.getUserStore().toString() ) );

        final Node node = Node.newNode().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( userKey ) ).
            rootDataSet( rootDataSet ).
            build();

        final User user = (User) PrincipalNodeTranslator.fromNode( node );
        assertEquals( "loginkey", user.getLogin() );
        assertEquals( "rmy@enonic.com", user.getEmail() );
        assertEquals( userKey, user.getKey() );
    }
}