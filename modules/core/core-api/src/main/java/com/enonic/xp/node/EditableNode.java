package com.enonic.xp.node;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;

@PublicApi
public class EditableNode
{
    public Node source;

    public PropertyTree data;

    public IndexConfigDocument indexConfigDocument;

    public Long manualOrderValue;

    public ChildOrder childOrder;

    public NodeType nodeType;

    public EditableNode( final Node source )
    {
        this.source = source;
        this.data = source.data().copy();
        this.indexConfigDocument = source.getIndexConfigDocument();
        this.manualOrderValue = source.getManualOrderValue();
        this.childOrder = source.getChildOrder();
        this.nodeType = source.getNodeType();
    }

    public Node build()
    {
        return Node.create( source ).
            data( data ).
            indexConfigDocument( indexConfigDocument ).
            manualOrderValue( manualOrderValue ).
            childOrder( childOrder ).
            nodeType( nodeType ).
            build();
    }
}
