package com.enonic.wem.api.item;


public class ItemCommands
{
    public CreateItem create()
    {
        return new CreateItem();
    }

    public UpdateItem update()
    {
        return new UpdateItem();
    }
}
