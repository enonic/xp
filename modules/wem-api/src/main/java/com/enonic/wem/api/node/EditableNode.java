package com.enonic.wem.api.node;


import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.security.acl.AccessControlList;

public class EditableNode
{
    private Node source;

    public NodeName name;

    public PropertyTree data;

    public Attachments attachments;

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
        this.attachments = source.attachments();
        this.indexConfigDocument = source.getIndexConfigDocument();
        this.manualOrderValue = source.getManualOrderValue();
        this.childOrder = source.getChildOrder();
        this.permissions = source.getPermissions();
        this.inheritPermissions = source.inheritsPermissions();
    }

    public Node build()
    {
        final Node.Builder builder = Node.newNode( source );
        builder.name( name );
        builder.data( data );
        builder.attachments( attachments );
        builder.indexConfigDocument( indexConfigDocument );
        builder.manualOrderValue( manualOrderValue );
        builder.childOrder( childOrder );
        builder.permissions( permissions );
        builder.inheritPermissions( inheritPermissions );
        return builder.build();
    }
}
