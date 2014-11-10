package com.enonic.wem.core.entity.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSetJson;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.core.entity.Attachments;
import com.enonic.wem.core.entity.IndexConfigDocumentJson;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.PatternBasedIndexConfigDocumentJson;

public final class NodeJson
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
    private AttachmentsJson attachments;

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
    private AccessControlListJson acl;

    @JsonProperty("effectiveAccessControlList")
    private AccessControlListJson effectiveAcl;

    public Node toNode()
    {
        return Node.newNode().
            id( NodeId.from( this.id ) ).
            name( NodeName.from( this.name ) ).
            creator( UserKey.from( this.creator ) ).
            modifier( this.modifier != null ? UserKey.from( this.modifier ) : null ).
            createdTime( this.createdTime ).
            modifiedTime( this.modifiedTime ).
            path( this.path ).
            parent( this.parent != null ? NodePath.newPath( this.parent ).build() : null ).
            rootDataSet( this.data.getRootDataSet() ).
            indexConfigDocument( this.indexConfigDocument.toEntityIndexConfig() ).
            attachments( this.attachments != null ? this.attachments.getAttachments() : Attachments.empty() ).
            childOrder( ChildOrder.from( this.childOrder ) ).
            manualOrderValue( this.manualOrderValue ).
            accessControlList( this.acl.getAcl() ).
            effectiveAcl( this.effectiveAcl.getAcl() ).
            build();
    }

    public static NodeJson toJson( final Node node )
    {
        final NodeJson json = new NodeJson();
        json.id = node.id().toString();
        json.createdTime = node.getCreatedTime();
        json.modifiedTime = node.getModifiedTime();
        json.data = new RootDataSetJson( node.data() );
        json.indexConfigDocument = createEntityIndexConfig( node.getIndexConfigDocument() );
        json.attachments = new AttachmentsJson( node.attachments() );
        json.name = node.name() != null ? node.name().toString() : null;
        json.parent = node.parent() != null ? node.parent().toString() : null;
        json.path = node.path() != null ? node.path().toString() : null;
        json.modifier = node.modifier() != null ? node.modifier().getQualifiedName() : null;
        json.creator = node.creator() != null ? node.creator().getQualifiedName() : null;
        json.childOrder = node.getChildOrder().toString();
        json.manualOrderValue = node.getManualOrderValue();
        json.acl = new AccessControlListJson( node.getAccessControlList() );
        json.effectiveAcl = new AccessControlListJson( node.getEffectiveAccessControlList() );
        return json;
    }

    private static IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }
}
