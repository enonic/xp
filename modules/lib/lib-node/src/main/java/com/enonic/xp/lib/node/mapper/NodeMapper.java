package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.lib.node.NodePropertyConstants;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Node;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeMapper
    implements MapSerializable
{
    private final Node node;

    public NodeMapper( final Node node )
    {
        this.node = node;
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
        serializeAttachedBinaries( gen, node.getAttachedBinaries() );
        gen.value( NodePropertyConstants.NODE_STATE, node.getNodeState().toString() );
        gen.value( NodePropertyConstants.NODE_TYPE, node.getNodeType().getName() );
        gen.value( NodePropertyConstants.NODE_VERSION_ID, node.getNodeVersionId() );
        gen.value( NodePropertyConstants.MANUAL_ORDER_VALUE, node.getManualOrderValue() );
        gen.value( NodePropertyConstants.TIMESTAMP, node.getTimestamp() );
        serializeData( gen, node.data() );
    }

    private static void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        new PropertyTreeMapper( value ).serialize( gen );
    }

    private static void serializeIndexConfigDocument( final MapGenerator gen, final IndexConfigDocument value )
    {
        gen.map( NodePropertyConstants.INDEX_CONFIG );
        new IndexConfigDocMapper( value ).serialize( gen );
        gen.end();
    }

    private static void serializePermissions( final MapGenerator gen, final Node node )
    {

        new PermissionsMapper( node ).serialize( gen );
    }

    private static void serializeAttachedBinaries( final MapGenerator gen, final AttachedBinaries attachedBinaries )
    {
        gen.array( NodePropertyConstants.ATTACHED_BINARIES );
        new AttachedBinariesMapper( attachedBinaries ).serialize( gen );
        gen.end();
    }

}