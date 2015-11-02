package com.enonic.xp.repo.impl.node.json;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

final class NodeVersionJson
{
    @JsonProperty("id")
    private String id;

    @JsonIgnore
    private String versionId;

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

    public NodeVersion fromJson( NodeVersionId nodeVersionId )
    {
        return NodeVersion.create().
            id( NodeId.from( this.id ) ).
            versionId( nodeVersionId ).
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

    public static NodeVersionJson toJson( final NodeVersion nodeVersion )
    {
        final NodeVersionJson json = new NodeVersionJson();
        json.id = nodeVersion.getId().toString();
        json.versionId = nodeVersion.getVersionId() != null ? nodeVersion.getVersionId().toString() : null;
        json.data = PropertyTreeJson.toJson( nodeVersion.getData() );
        json.indexConfigDocument = createEntityIndexConfig( nodeVersion.getIndexConfigDocument() );
        json.childOrder = nodeVersion.getChildOrder().toString();
        json.manualOrderValue = nodeVersion.getManualOrderValue();
        json.permissions = toJson( nodeVersion.getPermissions() );
        json.inheritPermissions = nodeVersion.isInheritPermissions();
        json.nodeType = nodeVersion.getNodeType().getName();
        json.attachedBinaries = toNodeAttachedBinaryJsonList( nodeVersion.getAttachedBinaries() );
        json.timestamp = nodeVersion.getTimestamp();
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
