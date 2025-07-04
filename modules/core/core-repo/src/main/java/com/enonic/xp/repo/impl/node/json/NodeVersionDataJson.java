package com.enonic.xp.repo.impl.node.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeVersion;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class NodeVersionDataJson
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("data")
    private List<PropertyArrayJson> data;

    @JsonProperty("childOrder")
    private String childOrder;

    @JsonProperty("manualOrderValue")
    private Long manualOrderValue;

    @JsonProperty("nodeType")
    private String nodeType;

    @JsonProperty("attachedBinaries")
    private List<AttachedBinaryJson> attachedBinaries;

    public List<PropertyArrayJson> getData()
    {
        return data;
    }

    public static NodeVersion fromJson( final NodeVersionDataJson json )
    {
        return NodeVersion.create()
            .id( NodeId.from( json.id ) )
            .data( PropertyTreeJson.fromJson( json.data ) )
            .childOrder( ChildOrder.from( json.childOrder ) )
            .manualOrderValue( json.manualOrderValue )
            .nodeType( NodeType.from( json.nodeType ) )
            .attachedBinaries( json.attachedBinaries.stream().map( AttachedBinaryJson::fromJson ).collect( AttachedBinaries.collector() ) )
            .build();
    }

    public static NodeVersionDataJson toJson( final NodeVersion nodeVersion )
    {
        final NodeVersionDataJson json = new NodeVersionDataJson();
        json.id = nodeVersion.getId().toString();
        json.data = PropertyTreeJson.toJson( nodeVersion.getData() );
        json.childOrder = nodeVersion.getChildOrder().toString();
        json.manualOrderValue = nodeVersion.getManualOrderValue();
        json.nodeType = nodeVersion.getNodeType().getName();
        json.attachedBinaries = toNodeAttachedBinaryJsonList( nodeVersion.getAttachedBinaries() );
        return json;
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
