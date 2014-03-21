package com.enonic.wem.api.entity;

import com.enonic.wem.api.data.RootDataSet;

public class CreateNodeParams
{
    private NodePath parent;

    private String name;

    private RootDataSet data;

    private Attachments attachments;

    private EntityIndexConfig entityIndexConfig;

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

    public CreateNodeParams entityIndexConfig( final EntityIndexConfig entityIndexConfig )
    {
        this.entityIndexConfig = entityIndexConfig;
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

    public EntityIndexConfig getEntityIndexConfig()
    {
        return entityIndexConfig;
    }

    public boolean isEmbed()
    {
        return embed;
    }

    public void validate()
    {

    }
}
