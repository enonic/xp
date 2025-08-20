package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeType;

import static com.enonic.xp.lib.node.NodePropertyConstants.CHILD_ORDER;
import static com.enonic.xp.lib.node.NodePropertyConstants.MANUAL_ORDER_VALUE;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_NAME;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_TYPE;
import static com.enonic.xp.lib.node.NodePropertyConstants.PARENT_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateNodeParamsFactoryTest
{
    @Test
    public void nodeName()
        throws Exception
    {
        final CreateNodeParams createNodeParams = createWithStringProperty( NODE_NAME, "myNode" );
        assertEquals( NodeName.from( "myNode" ), createNodeParams.getName() );
    }

    @Test
    public void parent_path()
        throws Exception
    {
        final CreateNodeParams createNodeParams = createWithStringProperty( PARENT_PATH, "/my/node/path" );
        assertEquals( "/my/node/path", createNodeParams.getParent().toString() );
    }

    @Test
    public void manual_order_value()
        throws Exception
    {
        final PropertyTree properties = new PropertyTree();
        properties.setLong( MANUAL_ORDER_VALUE, 3L );
        final CreateNodeParams createNodeParams = new CreateNodeParamsFactory().create( properties, BinaryAttachments.empty() );
        assertEquals( 3L, createNodeParams.getManualOrderValue().longValue() );
    }

    @Test
    public void child_order()
        throws Exception
    {
        final CreateNodeParams createNodeParams = createWithStringProperty( CHILD_ORDER, "_ts DESC" );
        assertEquals( ChildOrder.from( "_ts DESC" ), createNodeParams.getChildOrder() );
    }

    @Test
    public void nodeType()
        throws Exception
    {
        final CreateNodeParams createNodeParams = createWithStringProperty( NODE_TYPE, "myNodeType" );
        assertEquals( NodeType.from( "myNodeType" ), createNodeParams.getNodeType() );
    }

    private CreateNodeParams createWithStringProperty( final String nodeName, final String myNode )
    {
        final PropertyTree properties = new PropertyTree();
        properties.setString( nodeName, myNode );
        return new CreateNodeParamsFactory().create( new ScriptValueTranslatorResult( properties, BinaryAttachments.empty() ) );
    }
}
