package com.enonic.xp.node;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.security.acl.AccessControlList;

@Beta
public class EditableNode
{
    private Node source;

    public NodeName name;

    public PropertyTree data;

    public IndexConfigDocument indexConfigDocument;

    public Long manualOrderValue;

    public ChildOrder childOrder;

    public AccessControlList permissions;

    public boolean inheritPermissions;

    public EditableNode( final Node source )
    {
        this.source = source;
        this.name = source.name();
        this.data = source.data().copy();
        this.indexConfigDocument = source.getIndexConfigDocument();
        this.manualOrderValue = source.getManualOrderValue();
        this.childOrder = source.getChildOrder();
        this.permissions = source.getPermissions();
        this.inheritPermissions = source.inheritsPermissions();
    }

    public Node build()
    {
        return Node.newNode( source ).
            name( name ).
            data( data ).
            indexConfigDocument( indexConfigDocument ).
            manualOrderValue( manualOrderValue ).
            childOrder( childOrder ).
            permissions( permissions ).
            inheritPermissions( inheritPermissions ).
            build();
    }
}
