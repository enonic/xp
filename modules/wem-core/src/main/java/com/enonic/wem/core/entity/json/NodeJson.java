package com.enonic.wem.core.entity.json;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.data.RootDataSetJson;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.query.parser.QueryParser;
import com.enonic.wem.core.entity.Attachments;
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

    private String orderExpressions;

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
                     @JsonProperty("orderExpressions") final String orderExpressions )

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
        this.orderExpressions = orderExpressions;

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
            addOrderExpressions( toOrderExpressions() ).
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
        this.orderExpressions = getOrderExpressionsString( node.getOrderExpressions() );
    }

    private IndexConfigDocumentJson createEntityIndexConfig( final IndexConfigDocument indexConfig )
    {
        if ( indexConfig instanceof PatternIndexConfigDocument )
        {
            return new PatternBasedIndexConfigDocumentJson( (PatternIndexConfigDocument) indexConfig );
        }
        return null;
    }

    private String getOrderExpressionsString( final Set<OrderExpr> orderExpressions )
    {
        return orderExpressions.stream().
            map( OrderExpr::toString ).
            collect( Collectors.joining( ", " ) );
    }

    public Set<OrderExpr> toOrderExpressions()
    {
        final LinkedHashSet<OrderExpr> orderExpressions = Sets.newLinkedHashSet();
        final List<OrderExpr> orderExprs = QueryParser.parseOrderExpressions( this.orderExpressions );

        for ( final OrderExpr orderExpr : orderExprs )
        {
            orderExpressions.add( orderExpr );
        }

        return orderExpressions;
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
    public String getOrderExpressions()
    {
        return this.orderExpressions;
    }

    @JsonIgnore
    public Node getNode()
    {
        return node;
    }
}
