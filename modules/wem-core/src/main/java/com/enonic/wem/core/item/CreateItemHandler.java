package com.enonic.wem.core.item;


import org.joda.time.DateTime;

import com.enonic.wem.api.item.CreateItem;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.ItemJcrDao;

public class CreateItemHandler
    extends CommandHandler<CreateItem>
{
    public CreateItemHandler()
    {
        super( CreateItem.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateItem command )
        throws Exception
    {
        final ItemJcrDao itemDao = new ItemJcrDao( context.getJcrSession() );

        final ItemPath path = ItemPath.newItemPath( command.getParent(), command.getName() ).build();
        final Item item = Item.newItem().
            path( path ).
            name( command.getName() ).
            addDataSet( command.getDataSet() ).
            createdTime( DateTime.now() ).
            creator( command.getCreator() ).
            build();

        itemDao.storeNew( item );

        // TODO: index item or in dao?
    }
}
