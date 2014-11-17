package com.enonic.wem.core.entity.json;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.wem.api.data.RootDataSetJson;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.node.Attachment;
import com.enonic.wem.api.node.Attachments;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;

final class NodeJson
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("createdTime")
    private Instant createdTime;

    @JsonProperty("data")
    private RootDataSetJson data;

    @JsonProperty("modifiedTime")
    private Instant modifiedTime;

    @JsonProperty("indexConfigDocument")
    private IndexConfigDocumentJson indexConfigDocument;

    @JsonProperty("attachments")
    private List<AttachmentJson> attachments;

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

    @JsonProperty("accessControlList")
    private List<AccessControlEntryJson> aclList;

    @JsonProperty("effectiveAccessControlList")
    private List<AccessControlEntryJson> effectiveAclList;

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
            rootDataSet( this.data.getRootDataSet() ).
            indexConfigDocument( this.indexConfigDocument.fromJson() ).
            attachments( fromAttachmentJsonList( this.attachments ) ).
            childOrder( ChildOrder.from( this.childOrder ) ).
            manualOrderValue( this.manualOrderValue ).
            accessControlList( fromJson( this.aclList ) ).
            effectiveAcl( fromJson( this.effectiveAclList ) ).
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

    private Attachments fromAttachmentJsonList( final List<AttachmentJson> list )
    {
        final Attachments.Builder builder = Attachments.builder();
        for ( final AttachmentJson entry : list )
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
        json.data = new RootDataSetJson( node.data() );
        json.indexConfigDocument = createEntityIndexConfig( node.getIndexConfigDocument() );
        json.attachments = toAttachmentJsonList( node.attachments() );
        json.name = node.name() != null ? node.name().toString() : null;
        json.parent = node.parent() != null ? node.parent().toString() : null;
        json.path = node.path() != null ? node.path().toString() : null;
        json.modifier = node.modifier() != null ? node.modifier().toString() : null;
        json.creator = node.creator() != null ? node.creator().toString() : null;
        json.childOrder = node.getChildOrder().toString();
        json.manualOrderValue = node.getManualOrderValue();
        json.aclList = toJson( node.getAccessControlList() );
        json.effectiveAclList = toJson( node.getEffectiveAccessControlList() );
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

    private static List<AttachmentJson> toAttachmentJsonList( final Attachments attachments )
    {
        if ( attachments == null )
        {
            return null;
        }

        final List<AttachmentJson> attachmentJsons = Lists.newArrayList();
        for ( final Attachment attachment : attachments )
        {
            attachmentJsons.add( AttachmentJson.toJson( attachment ) );
        }

        return attachmentJsons;
    }
}
