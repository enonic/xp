package com.enonic.xp.node;


import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;


public final class EditableNode
{
    public Node source;

    public PropertyTree data;

    public IndexConfigDocument indexConfigDocument;

    /**
     * @deprecated Manual order values are superseded by order keys and are not edited. Scheduled for removal.
     */
    @Deprecated
    public Long manualOrderValue;

    public ChildOrder childOrder;

    /**
     * The order key, settable here and nowhere else outside the server: node patching is the sanctioned way to write an
     * exact placement, for replication and repair. An unchanged value keeps the node where it is.
     */
    public String orderKey;

    public NodeType nodeType;

    public EditableNode( final Node source )
    {
        this.source = source;
        this.data = source.data().copy();
        this.indexConfigDocument = source.getIndexConfigDocument();
        this.manualOrderValue = source.getManualOrderValue();
        this.childOrder = source.getChildOrder();
        this.orderKey = source.getOrderKey();
        this.nodeType = source.getNodeType();
    }

    public Node build()
    {
        return Node.create( source )
            .data( data )
            .indexConfigDocument( indexConfigDocument )
            .manualOrderValue( manualOrderValue )
            .childOrder( childOrder )
            .orderKey( orderKey )
            .nodeType( nodeType )
            .build();
    }
}
