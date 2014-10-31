package com.enonic.wem.core.security;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;

import static org.junit.Assert.*;

public class GroupNodeTranslatorTest
{

    @Test
    public void toCreateNode()
        throws Exception
    {
        final Group group = Group.create().
            displayName( "My Group" ).
            key( PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" ) ).
            build();

        final CreateNodeParams createNodeParams = GroupNodeTranslator.toCreateNodeParams( group );

        assertEquals( "group-a", createNodeParams.getName() );

        final RootDataSet rootDataSet = createNodeParams.getData();
        assertEquals( 4, rootDataSet.size() );
        assertEquals( UserStoreKey.system().toString(), rootDataSet.getProperty( PrincipalNodeTranslator.USERSTORE_KEY ).getString() );
        assertEquals( PrincipalType.GROUP.toString(), rootDataSet.getProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY ).getString() );
        assertEquals( "My Group", rootDataSet.getProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY ).getString() );
        assertNotNull( rootDataSet );

    }


    @Test
    public void toGroup()
        throws Exception
    {
        final PrincipalKey groupKey = PrincipalKey.ofGroup( UserStoreKey.system(), "group-a" );

        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( PrincipalNodeTranslator.DISPLAY_NAME_KEY, Value.newString( "Group A" ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY, Value.newString( groupKey.getType().toString() ) );
        rootDataSet.setProperty( PrincipalNodeTranslator.USERSTORE_KEY, Value.newString( groupKey.getUserStore().toString() ) );

        final Node node = Node.newNode().
            id( NodeId.from( "id" ) ).
            name( PrincipalKeyNodeTranslator.toNodeName( groupKey ) ).
            rootDataSet( rootDataSet ).
            build();

        final Group group = GroupNodeTranslator.fromNode( node );
        assertEquals( groupKey, group.getKey() );
    }


}