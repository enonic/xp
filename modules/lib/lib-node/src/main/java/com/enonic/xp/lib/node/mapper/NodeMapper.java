package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
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
        gen.value( "id", node.id() );
        gen.value( "name", node.name() );
        gen.value( "path", node.path() );
        serializeAttachedBinaries( gen, node.getAttachedBinaries() );
        gen.value( "childOrder", node.getChildOrder().toString() );
        serializeData( gen, node.data() );
        serializeIndexConfigDocument( gen, node.getIndexConfigDocument() );
        serializePermissions( gen, node );
        gen.value( "nodeState", node.getNodeState() );
        gen.value( "nodeType", node.getNodeType() );
        gen.value( "nodeVersionId", node.getNodeVersionId() );
        gen.value( "manualOrderValue", node.getManualOrderValue() );
        gen.value( "timestamp", node.getTimestamp() );
    }

    private static void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        gen.map( "data" );
        new PropertyTreeMapper( value ).serialize( gen );
        gen.end();
    }

    private static void serializeAttachedBinaries( final MapGenerator gen, final AttachedBinaries attachedBinaries )
    {
        new AttachedBinariesMapper( attachedBinaries ).serialize( gen );
    }

    private static void serializeIndexConfigDocument( final MapGenerator gen, final IndexConfigDocument value )
    {
        new IndexConfigDocMapper( value ).serialize( gen );
    }

    private static void serializePermissions( final MapGenerator gen, final Node node )
    {
        new PermissionsMapper( node ).serialize( gen );
    }
}
