package com.enonic.wem.api.item;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.data.RootDataSet;


public class CreateItem
    extends Command<CreateItemResult>
{
    private UserKey creator;

    private String name;

    private ItemPath parent;

    private Icon icon;

    private RootDataSet data;

    public CreateItem creator( final UserKey value )
    {
        this.creator = value;
        return this;
    }

    public CreateItem name( final String value )
    {
        this.name = value;
        return this;
    }

    public CreateItem parent( final ItemPath value )
    {
        this.parent = value;
        return this;
    }

    public CreateItem parent( final String value )
    {
        this.parent = new ItemPath( value );
        return this;
    }

    public CreateItem icon( final Icon value )
    {
        this.icon = value;
        return this;
    }

    public CreateItem data( final RootDataSet value )
    {
        this.data = value;
        return this;
    }

    public UserKey getCreator()
    {
        return creator;
    }

    public String getName()
    {
        return name;
    }

    public ItemPath getParent()
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
