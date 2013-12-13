package com.enonic.wem.api.command.entity;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NodePath;


public class CreateNode
    extends Command<CreateNodeResult>
{
    private NodePath parent;

    private String name;

    private RootDataSet data;

    private Attachments attachments;

    private EntityIndexConfig entityIndexConfig;

    private boolean embed;

    public CreateNode parent( final NodePath value )
    {
        this.parent = value;
        return this;
    }

    public CreateNode parent( final String value )
    {
        this.parent = new NodePath( value );
        return this;
    }

    public CreateNode name( final String value )
    {
        this.name = value;
        return this;
    }

    public CreateNode data( final RootDataSet value )
    {
        this.data = value;
        return this;
    }

    public CreateNode attachments( final Attachments value )
    {
        this.attachments = value;
        return this;
    }

    public CreateNode entityIndexConfig( final EntityIndexConfig entityIndexConfig )
    {
        this.entityIndexConfig = entityIndexConfig;
        return this;
    }

    public CreateNode embed( final boolean embed )
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

    @Override
    public void validate()
    {

    }
}
