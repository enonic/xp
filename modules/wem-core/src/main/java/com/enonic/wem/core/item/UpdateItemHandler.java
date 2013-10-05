package com.enonic.wem.core.item;


import javax.jcr.Session;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.UpdateItem;
import com.enonic.wem.api.item.UpdateItemResult;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.ItemJcrDao;
import com.enonic.wem.core.item.dao.UpdateItemArgs;

import static com.enonic.wem.core.item.dao.UpdateItemArgs.newUpdateItemArgs;

public class UpdateItemHandler
    extends CommandHandler<UpdateItem>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();

        final ItemJcrDao itemDao = new ItemJcrDao( session );

        final Item persisted = itemDao.getItemById( command.getItemToUpdate() );

        Item edited = command.getEditor().edit( persisted );
        if ( edited == null )
        {
            // TODO: set status NO CHANGE?
            return;
        }

        persisted.checkIllegalEdit( edited );

        final UpdateItemArgs updateItemArgs = newUpdateItemArgs().
            itemToUpdate( command.getItemToUpdate() ).
            name( edited.name() ).
            build();

        final Item persistedItem = itemDao.updateItem( updateItemArgs );
        session.save();
        // TODO: update index for item or in dao?

        command.setResult( new UpdateItemResult( persistedItem ) );
    }
}
