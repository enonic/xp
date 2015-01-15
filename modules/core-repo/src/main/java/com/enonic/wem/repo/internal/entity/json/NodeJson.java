package com.enonic.wem.repo.internal.entity.json;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTreeJson;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

final class NodeJson
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("createdTime")
    private Instant createdTime;

    @JsonProperty("data")
    private List<PropertyArrayJson> data;

    @JsonProperty("modifiedTime")
    private Instant modifiedTime;

    @JsonProperty("indexConfigDocument")
    private IndexConfigDocumentJson indexConfigDocument;

    @JsonProperty("name")
    private String name;

    @JsonProperty("parent")
    private String parent;

    @JsonProperty("path")
    private String path;

    @JsonProperty("modifier")
    private String modifier;

    @JsonProperty("creator")
    private String creator;

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

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("language")
    private String language;

    public Node fromJson()
    {
        return Node.newNode().
            id( NodeId.from( this.id ) ).
            name( NodeName.from( this.name ) ).
            creator( PrincipalKey.from( this.creator ) ).
            modifier( this.modifier != null ? PrincipalKey.from( this.modifier ) : null ).
            createdTime( this.createdTime ).
            modifiedTime( this.modifiedTime ).
            path( this.path ).
            parent( this.parent != null ? NodePath.newPath( this.parent ).build() : null ).
            data( PropertyTreeJson.fromJson( this.data ) ).
            indexConfigDocument( this.indexConfigDocument.fromJson() ).
            childOrder( ChildOrder.from( this.childOrder ) ).
            manualOrderValue( this.manualOrderValue ).
            permissions( fromJson( this.permissions ) ).
            inheritPermissions( this.inheritPermissions ).
            nodeType( NodeType.from( this.nodeType ) ).
            attachedBinaries( fromNodeAttahcedBinaryJsonList( attachedBinaries ) ).
            owner( this.owner != null ? PrincipalKey.from( this.owner ) : null ).
            language( this.language != null ? Locale.forLanguageTag( this.language ) : null ).
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

    private AttachedBinaries fromNodeAttahcedBinaryJsonList( final List<AttachedBinaryJson> list )
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
        json.createdTime = node.getCreatedTime();
        json.modifiedTime = node.getModifiedTime();
        json.data = PropertyTreeJson.toJson( node.data() );
        json.indexConfigDocument = createEntityIndexConfig( node.getIndexConfigDocument() );
        json.name = node.name() != null ? node.name().toString() : null;
        json.parent = node.parent() != null ? node.parent().toString() : null;
        json.path = node.path() != null ? node.path().toString() : null;
        json.modifier = node.modifier() != null ? node.modifier().toString() : null;
        json.creator = node.creator() != null ? node.creator().toString() : null;
        json.childOrder = node.getChildOrder().toString();
        json.manualOrderValue = node.getManualOrderValue();
        json.permissions = toJson( node.getPermissions() );
        json.inheritPermissions = node.inheritsPermissions();
        json.nodeType = node.getNodeType().getName();
        json.attachedBinaries = toNodeAttachedBinaryJsonList( node.getAttachedBinaries() );
        json.owner = node.getOwner() != null ? node.getOwner().toString() : null;
        json.language = node.getLanguage() != null ? node.getLanguage().toLanguageTag() : null;
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
}
