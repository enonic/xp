package com.enonic.xp.repo.impl.entity.json;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

final class NodeJson
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("data")
    private List<PropertyArrayJson> data;

    @JsonProperty("indexConfigDocument")
    private IndexConfigDocumentJson indexConfigDocument;

    @JsonProperty("childOrder")
    private String childOrder;

    @JsonProperty("manualOrderValue")
    private Long manualOrderValue;

    @JsonProperty("permissions")
    private List<AccessControlEntryJson> permissions;

    @JsonProperty("inheritPermissions")
    private boolean inheritPermissions;

    @JsonProperty("nodeType")
    private String nodeType;

    @JsonProperty("attachedBinaries")
    private List<AttachedBinaryJson> attachedBinaries;

    public Node fromJson()
    {
        return Node.create().
            id( NodeId.from( this.id ) ).
            data( PropertyTreeJson.fromJson( this.data ) ).
            indexConfigDocument( this.indexConfigDocument.fromJson() ).
            childOrder( ChildOrder.from( this.childOrder ) ).
            manualOrderValue( this.manualOrderValue ).
            permissions( fromJson( this.permissions ) ).
            inheritPermissions( this.inheritPermissions ).
            nodeType( NodeType.from( this.nodeType ) ).
            attachedBinaries( fromNodeAttachedBinaryJsonList( attachedBinaries ) ).
            timestamp( this.timestamp ).
            build();
    }

    private AccessControlList fromJson( final List<AccessControlEntryJson> list )
    {
        final AccessControlList.Builder builder = AccessControlList.create();
        for ( final AccessControlEntryJson entryJson : list )
        {
            builder.add( entryJson.fromJson() );
        }

        return builder.build();
    }

    private AttachedBinaries fromNodeAttachedBinaryJsonList( final List<AttachedBinaryJson> list )
    {
        final AttachedBinaries.Builder builder = AttachedBinaries.create();
        for ( final AttachedBinaryJson entry : list )
        {
            builder.add( entry.fromJson() );
        }

        return builder.build();
    }

    public static NodeJson toJson( final Node node )
    {
        final NodeJson json = new NodeJson();
        json.id = node.id().toString();
        json.data = PropertyTreeJson.toJson( node.data() );
        json.indexConfigDocument = createEntityIndexConfig( node.getIndexConfigDocument() );
        json.childOrder = node.getChildOrder().toString();
        json.manualOrderValue = node.getManualOrderValue();
        json.permissions = toJson( node.getPermissions() );
        json.inheritPermissions = node.inheritsPermissions();
        json.nodeType = node.getNodeType().getName();
        json.attachedBinaries = toNodeAttachedBinaryJsonList( node.getAttachedBinaries() );
        json.timestamp = node.getTimestamp();
        return json;
    }

    private static IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return IndexConfigDocumentJson.toJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }

    private static List<AccessControlEntryJson> toJson( final AccessControlList acl )
    {
        if ( acl == null )
        {
            return null;
        }

        final List<AccessControlEntryJson> entryJsonList = Lists.newArrayList();
        for ( final AccessControlEntry entry : acl )
        {
            entryJsonList.add( AccessControlEntryJson.toJson( entry ) );
        }
        return entryJsonList;
    }

    private static List<AttachedBinaryJson> toNodeAttachedBinaryJsonList( final AttachedBinaries attachedBinaries )
    {
        if ( attachedBinaries == null )
        {
            return null;
        }

        final List<AttachedBinaryJson> attachedBinaryJsons = Lists.newArrayList();

        for ( final AttachedBinary attachedBinary : attachedBinaries )
        {
            attachedBinaryJsons.add( AttachedBinaryJson.toJson( attachedBinary ) );
        }

        return attachedBinaryJsons;
    }

    public String getId()
    {
        return id;
    }
}
