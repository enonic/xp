package com.enonic.wem.api.command.entity;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.NodePath;


public class CreateNode
    extends Command<CreateNodeResult>
{
    private NodePath parent;

    private String name;

    private Icon icon;

    private RootDataSet data;

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

    public CreateNode icon( final Icon value )
    {
        this.icon = value;
        return this;
    }

    public CreateNode data( final RootDataSet value )
    {
        this.data = value;
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

    public Icon getIcon()
    {
        return icon;
    }

    public RootDataSet getData()
    {
        return data;
    }

    @Override
    public void validate()
    {

    }
}
