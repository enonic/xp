package com.enonic.wem.core.item;


import javax.inject.Inject;

import org.joda.time.DateTime;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.UpdateItem;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.ItemDao;

public class UpdateItemHandler
    extends CommandHandler<UpdateItem>
{
    private ItemDao itemDao;

    public UpdateItemHandler()
    {
        super( UpdateItem.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateItem command )
        throws Exception
    {
        final Item persisted = itemDao.getItemById( command.getItemId() );

        Item edited = command.getEditor().edit( persisted );
        if ( edited == null )
        {
            // TODO: set status NO CHANGE?
            return;
        }

        persisted.checkIllegalEdit( edited );

        edited = Item.newItem( edited ).
            modifiedTime( DateTime.now() ).
            modifier( command.getModifier() ).
            build();

        itemDao.updateExisting( edited );

        // TODO: update index for item or in dao?
    }

    @Inject
    public void setItemDao( final ItemDao itemDao )
    {
        this.itemDao = itemDao;
    }
}
