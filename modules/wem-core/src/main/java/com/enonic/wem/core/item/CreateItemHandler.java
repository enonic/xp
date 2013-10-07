package com.enonic.wem.core.item;


import javax.jcr.Session;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.item.CreateItem;
import com.enonic.wem.api.item.CreateItemResult;
import com.enonic.wem.api.item.Item;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.CreateItemArgs;
import com.enonic.wem.core.item.dao.ItemJcrDao;

public class CreateItemHandler
    extends CommandHandler<CreateItem>
{
    @Override
    public void handle()
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ItemJcrDao itemDao = new ItemJcrDao( session );

        final CreateItemArgs createItemArgs = CreateItemArgs.newCreateItemArgs().
            creator( UserKey.superUser() ).
            parent( command.getParent() ).
            name( command.getName() ).
            icon( command.getIcon() ).
            rootDataSet( command.getData() ).
            build();

        final Item persistedItem = itemDao.createItem( createItemArgs );
        session.save();

        command.setResult( new CreateItemResult( persistedItem ) );
        // TODO: index item or in dao?
    }
}
