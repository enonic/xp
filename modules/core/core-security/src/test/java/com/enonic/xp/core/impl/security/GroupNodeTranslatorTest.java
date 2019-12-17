package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
            key( PrincipalKey.ofGroup( IdProviderKey.system(), "group-a" ) ).
            modifiedTime( Instant.now( clock ) ).
            description( "my group a" ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( group );

        assertEquals( "group-a", createNodeParams.getName() );

        final PropertyTree rootDataSet = createNodeParams.getData();
        assertNotNull( rootDataSet );
        assertEquals( 4, rootDataSet.getTotalSize() );
        assertEquals( IdProviderKey.system().toString(), rootDataSet.getString( PrincipalPropertyNames.ID_PROVIDER_KEY ) );
        assertEquals( PrincipalType.GROUP.toString(), rootDataSet.getString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY ) );
        assertEquals( "My Group", rootDataSet.getString( PrincipalPropertyNames.DISPLAY_NAME_KEY ) );
        assertEquals( "my group a", rootDataSet.getString( PrincipalPropertyNames.DESCRIPTION_KEY ) );
    }


    @Test
    public void toGroup()
        throws Exception
    {
        final PrincipalKey groupKey = PrincipalKey.ofGroup( IdProviderKey.system(), "group-a" );

        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, "Group A" );
        rootDataSet.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, groupKey.getType().toString() );
        rootDataSet.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, groupKey.getIdProviderKey().toString() );

        final Node node = Node.create().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( groupKey ) ).
            data( rootDataSet ).
            build();

        final Group group = PrincipalNodeTranslator.groupFromNode( node );
        assertEquals( groupKey, group.getKey() );
    }


}
