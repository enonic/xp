package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.xp.core.impl.security.PrincipalKeyNodeTranslator;
import com.enonic.xp.core.impl.security.PrincipalNodeTranslator;
import com.enonic.xp.core.impl.security.PrincipalPropertyNames;

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

        final PropertyTree rootDataSet = createNodeParams.getData();
        assertNotNull( rootDataSet );
        assertEquals( 3, rootDataSet.getTotalSize() );
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getString( PrincipalPropertyNames.USER_STORE_KEY ) );
        assertEquals( PrincipalType.GROUP.toString(), rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) );
        assertEquals( "My Group", rootDataSet.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) );
    }


    @Test
    public void toGroup()
        throws Exception
    {
        final PrincipalKey groupKey = PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" );

        final PropertyTree rootDataSet = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        rootDataSet.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Group A" );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, groupKey.getType().toString() );
        rootDataSet.setString( PrincipalPropertyNames.USER_STORE_KEY, groupKey.getUserStore().toString() );

        final Node node = Node.newNode().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( groupKey ) ).
            data( rootDataSet ).
            build();

        final Group group = PrincipalNodeTranslator.groupFromNode( node );
        assertEquals( groupKey, group.getKey() );
    }


}