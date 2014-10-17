package com.enonic.wem.core.entity.json;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSetJson;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.parser.QueryParser;
import com.enonic.wem.core.entity.Attachments;
import com.enonic.wem.core.entity.ChildOrder;
import com.enonic.wem.core.entity.IndexConfigDocumentJson;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.PatternBasedIndexConfigDocumentJson;

public class NodeJson
{
    protected String id;

    protected Instant createdTime;

    protected RootDataSetJson data;

    protected Instant modifiedTime;

    protected IndexConfigDocumentJson indexConfigDocument;

    private AttachmentsJson attachments;

    private Node node;

    private String name;

    private String parent;

    private String path;

    private String modifier;

    private String creator;

    private String childOrder;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public NodeJson( @JsonProperty("name") final String name, //
                     @JsonProperty("parent") final String parent,  //
                     @JsonProperty("path") final String path,    //
                     @JsonProperty("modifier") final String modifier, //
                     @JsonProperty("creator") final String creator, //
                     @JsonProperty("id") final String id, //
                     @JsonProperty("createdTime") final Instant createdTime, //
                     @JsonProperty("data") final RootDataSetJson data, //
                     @JsonProperty("modifiedTime") final Instant modifiedTime, //
                     @JsonProperty("indexConfigDocument") final IndexConfigDocumentJson indexConfigDocument,
                     @JsonProperty("attachments") final AttachmentsJson attachments, //
                     @JsonProperty("childOrder") final String childOrder )

    {
        this.id = id;
        this.createdTime = createdTime;
        this.data = data;
        this.modifiedTime = modifiedTime;
        this.indexConfigDocument = indexConfigDocument;
        this.attachments = attachments;

        this.name = name;
        this.parent = parent;
        this.path = path;
        this.modifier = modifier;
        this.creator = creator;
        this.childOrder = childOrder;

        this.node = Node.newNode().
            id( NodeId.from( id ) ).
            name( NodeName.from( name ) ).
            creator( UserKey.from( creator ) ).
            modifier( modifier != null ? UserKey.from( modifier ) : null ).
            createdTime( createdTime ).
            modifiedTime( modifiedTime ).
            path( path ).
            parent( parent != null ? NodePath.newPath( parent ).build() : null ).
            rootDataSet( data.getRootDataSet() ).
            indexConfigDocument( indexConfigDocument.toEntityIndexConfig() ).
            attachments( attachments != null ? attachments.getAttachments() : Attachments.empty() ).
            childOrder( toChildOrder() ).
            build();
    }

    public NodeJson( final Node node )
    {
        this.id = node.id().toString();
        this.createdTime = node.getCreatedTime();
        this.modifiedTime = node.getModifiedTime();
        this.data = new RootDataSetJson( node.data() );
        this.indexConfigDocument = createEntityIndexConfig( node.getIndexConfigDocument() );
        this.attachments = new AttachmentsJson( node.attachments() );
        this.node = node;
        this.name = node.name() != null ? node.name().toString() : null;
        this.parent = node.parent() != null ? node.parent().toString() : null;
        this.path = node.path() != null ? node.path().toString() : null;
        this.modifier = node.modifier() != null ? node.modifier().getQualifiedName() : null;
        this.creator = node.creator() != null ? node.creator().getQualifiedName() : null;
        this.childOrder = node.getChildOrder().toString();
    }

    private IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }

    public ChildOrder toChildOrder()
    {
        final ChildOrder.Builder builder = ChildOrder.create();

        if ( Strings.isNullOrEmpty( this.childOrder ) )
        {
            return builder.build();
        }

        final List<OrderExpr> orderExprs = QueryParser.parseOrderExpressions( this.childOrder );

        for ( final OrderExpr orderExpr : orderExprs )
        {
            builder.add( orderExpr );
        }

        return builder.build();
    }


    @SuppressWarnings("UnusedDeclaration")
    public String getName()
    {
        return name;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getParent()
    {
        return parent;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getPath()
    {
        return path;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getModifier()
    {
        return modifier;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCreator()
    {
        return creator;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getCreatedTime()
    {
        return createdTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public RootDataSetJson getData()
    {
        return data;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IndexConfigDocumentJson getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

    @SuppressWarnings("UnusedDeclaration")
    public AttachmentsJson getAttachments()
    {
        return attachments;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getChildOrder()
    {
        return this.childOrder;
    }

    @JsonIgnore
    public Node getNode()
    {
        return node;
    }
}
