package com.enonic.wem.core.item;


import org.joda.time.DateTime;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.UpdateItem;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.ItemJcrDao;

public class UpdateItemHandler
    extends CommandHandler<UpdateItem>
{
    public UpdateItemHandler()
    {
        super( UpdateItem.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateItem command )
        throws Exception
    {
        final ItemJcrDao itemDao = new ItemJcrDao( context.getJcrSession() );

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

}
