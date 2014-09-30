package com.enonic.wem.core.entity;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.index.IndexConfigDocument;

public class CreateNodeParams
{
    private NodePath parent;

    private String name;

    private RootDataSet data;

    private Attachments attachments;

    private IndexConfigDocument indexConfigDocument;

    private boolean embed;

    public CreateNodeParams parent( final NodePath value )
    {
        this.parent = value;
        return this;
    }

    public CreateNodeParams parent( final String value )
    {
        this.parent = new NodePath( value );
        return this;
    }

    public CreateNodeParams name( final String value )
    {
        this.name = value;
        return this;
    }

    public CreateNodeParams data( final RootDataSet value )
    {
        this.data = value;
        return this;
    }

    public CreateNodeParams attachments( final Attachments value )
    {
        this.attachments = value;
        return this;
    }

    public CreateNodeParams indexConfigDocument( final IndexConfigDocument indexConfigDocument )
    {
        this.indexConfigDocument = indexConfigDocument;
        return this;
    }


    public CreateNodeParams embed( final boolean embed )
    {
        this.embed = embed;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public RootDataSet getData()
    {
        return data;
    }

    public Attachments getAttachments()
    {
        return attachments;
    }

    public IndexConfigDocument getIndexConfigDocument()
    {
        return indexConfigDocument;
    }

}
