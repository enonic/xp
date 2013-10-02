package com.enonic.wem.core.item;


import javax.inject.Inject;

import org.joda.time.DateTime;

import com.enonic.wem.api.item.CreateItem;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.ItemDao;

public class CreateItemHandler
    extends CommandHandler<CreateItem>
{
    private ItemDao itemDao;

    public CreateItemHandler()
    {
        super( CreateItem.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateItem command )
        throws Exception
    {
        final Item item = Item.newItem().
            name( command.getName() ).
            addDataSet( command.getDataSet() ).
            createdTime( DateTime.now() ).
            creator( command.getCreator() ).
            build();

        itemDao.storeNew( item, command.getParent() );

        // TODO: index item or in dao?
    }

    @Inject
    public void setItemDao( final ItemDao itemDao )
    {
        this.itemDao = itemDao;
    }
}
