package com.enonic.wem.core.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;

import static org.junit.Assert.*;

public class GroupNodeTranslatorTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void toCreateNode()
        throws Exception
    {
        final Group group = Group.create().
            displayName( "My Group" ).
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( group );

        assertEquals( "group-a", createNodeParams.getName() );

        final RootDataSet rootDataSet = createNodeParams.getData();
        assertNotNull( rootDataSet );
        assertEquals( 3, rootDataSet.size() );
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getProperty( PrincipalNodeTranslator.USER_STORE_KEY ).getString() );
        assertEquals( PrincipalType.GROUP.toString(), rootDataSet.getProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY ).getString() );
        assertEquals( "My Group", rootDataSet.getProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY ).getString() );
    }


    @Test
    public void toGroup()
        throws Exception
    {
        final PrincipalKey groupKey = PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" );

        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY, Value.newString( "Group A" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, Value.newString( groupKey.getType().toString() ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.USER_STORE_KEY, Value.newString( groupKey.getUserStore().toString() ) );

        final Node node = Node.newNode().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( groupKey ) ).
            rootDataSet( rootDataSet ).
            modifiedTime( Instant.now( clock ) ).
            build();

        final Group group = PrincipalNodeTranslator.groupFromNode( node );
        assertEquals( groupKey, group.getKey() );
    }


}