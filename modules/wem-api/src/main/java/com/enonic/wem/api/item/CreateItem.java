package com.enonic.wem.api.item;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.data.RootDataSet;


public class CreateItem
    extends Command<CreateItemResult>
{
    private UserKey creator;

    private String name;

    private ItemPath parent;

    private RootDataSet dataSet;

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

    public CreateItem dataSet( final RootDataSet value )
    {
        this.dataSet = value;
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

    public RootDataSet getDataSet()
    {
        return dataSet;
    }

    @Override
    public void validate()
    {

    }
}
