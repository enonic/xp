package com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import com.enonic.xp.repo.impl.node.json.AccessControlEntryJson;
import com.enonic.xp.repo.impl.node.json.AttachedBinaryJson;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Pre4NodeVersionJson
{
    @JsonProperty("id")
    private String id;

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

    @JsonProperty("nodeType")
    private String nodeType;

    @JsonProperty("attachedBinaries")
    private List<AttachedBinaryJson> attachedBinaries;

    public IndexConfigDocumentJson getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return permissions;
    }

    public NodeVersion fromJson()
    {
        return NodeVersion.create()
            .id( NodeId.from( this.id ) )
            .data( PropertyTreeJson.fromJson( this.data ) )
            .indexConfigDocument( IndexConfigDocumentJson.fromJson( indexConfigDocument ) )
            .childOrder( ChildOrder.from( this.childOrder ) )
            .manualOrderValue( this.manualOrderValue )
            .permissions( fromJson( this.permissions ) )
            .nodeType( NodeType.from( this.nodeType ) )
            .attachedBinaries( attachedBinaries.stream().map( AttachedBinaryJson::fromJson ).collect( AttachedBinaries.collector() ) )
            .build();
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

    public static Pre4NodeVersionJson toJson( final NodeVersion nodeVersion )
    {
        final Pre4NodeVersionJson json = new Pre4NodeVersionJson();
        json.id = nodeVersion.getId().toString();
        json.data = PropertyTreeJson.toJson( nodeVersion.getData() );
        json.indexConfigDocument = createEntityIndexConfig( nodeVersion.getIndexConfigDocument() );
        json.childOrder = nodeVersion.getChildOrder().toString();
        json.manualOrderValue = nodeVersion.getManualOrderValue();
        json.permissions = toJson( nodeVersion.getPermissions() );
        json.nodeType = nodeVersion.getNodeType().getName();
        json.attachedBinaries = toNodeAttachedBinaryJsonList( nodeVersion.getAttachedBinaries() );
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

        final List<AccessControlEntryJson> entryJsonList = new ArrayList<>();
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

        final List<AttachedBinaryJson> attachedBinaryJsons = new ArrayList<>();

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
