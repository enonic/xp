package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class DuplicateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    @Test
    public void testExample()
    {
        final PropertyTree dataTree = new PropertyTree();
        final PropertySet data = dataTree.newSet();
        data.addString( "prop1", "Value 1" );
        data.addString( "extraProp", "extraPropValue" );

        dataTree.addSet( "data", data );

        final Node node = Node.create()
            .id( NodeId.from( "nodeid-copy" ) )
            .name( "duplicated-node" )
            .parentPath( NodePath.ROOT )
            .data( dataTree )
            .permissions(
                AccessControlList.create().add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ).build() )
            .build();

        final ArgumentCaptor<DuplicateNodeParams> captor = ArgumentCaptor.forClass( DuplicateNodeParams.class );

        when( nodeService.duplicate( captor.capture() ) ).thenReturn( node );

        runScript( "/lib/xp/examples/node/duplicate.js" );

        final DuplicateNodeParams value = captor.getValue();
        assertThat( value.getNodeId() ).isEqualTo( NodeId.from( "nodeId" ) );
        assertThat( value.getIncludeChildren() ).isEqualTo( false );
        assertThat( value.getParent() ).isEqualTo( NodePath.ROOT );
        assertThat( value.getRefresh() ).isEqualTo( RefreshMode.SEARCH );
        assertThat( value.getDataProcessor() ).isNotNull();
        assertThat( value.getProcessor() ).isNull();
    }

    @Test
    public void testInvalidParams()
    {
        runFunction( "/test/DuplicateNodeHandlerTest.js", "testInvalidParams" );
    }
}
