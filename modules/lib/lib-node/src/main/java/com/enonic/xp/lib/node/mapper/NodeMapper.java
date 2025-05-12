package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.lib.node.NodePropertyConstants;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeMapper
    implements MapSerializable
{
    private final Node node;

    private final boolean useRawValues;


    public NodeMapper( final Node node )
    {
        this.node = node;
        this.useRawValues = false;
    }

    public NodeMapper( final Node node, final boolean useRawValues )
    {
        this.node = node;
        this.useRawValues = useRawValues;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( NodePropertyConstants.NODE_ID, node.id() );
        gen.value( NodePropertyConstants.NODE_NAME, node.name() );
        gen.value( NodePropertyConstants.PATH, node.path() );
        gen.value( NodePropertyConstants.CHILD_ORDER, node.getChildOrder().toString() );
        serializeIndexConfigDocument( gen, node.getIndexConfigDocument() );
        serializePermissions( gen, node );
        gen.value( NodePropertyConstants.NODE_TYPE, node.getNodeType().getName() );
        gen.value( NodePropertyConstants.NODE_VERSION_ID, node.getNodeVersionId() );
        gen.value( NodePropertyConstants.MANUAL_ORDER_VALUE, node.getManualOrderValue() );
        gen.value( NodePropertyConstants.TIMESTAMP, node.getTimestamp() );
        serializeData( gen, node.data() );
    }

    private void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        new PropertyTreeMapper( this.useRawValues, value ).serialize( gen );
    }

    private void serializeIndexConfigDocument( final MapGenerator gen, final IndexConfigDocument value )
    {
        gen.map( NodePropertyConstants.INDEX_CONFIG );
        new IndexConfigDocMapper( value ).serialize( gen );
        gen.end();
    }

    private void serializePermissions( final MapGenerator gen, final Node node )
    {
        new PermissionsMapper( node ).serialize( gen );
    }


}
