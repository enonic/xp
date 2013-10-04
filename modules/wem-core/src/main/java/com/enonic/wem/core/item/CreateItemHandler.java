package com.enonic.wem.core.item;


import javax.jcr.Session;

import com.enonic.wem.api.item.CreateItem;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.item.dao.CreateItemArgs;
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
        final Session session = context.getJcrSession();
        final ItemJcrDao itemDao = new ItemJcrDao( session );

        final CreateItemArgs createItemArgs = CreateItemArgs.newCreateItemArgs().
            creator( command.getCreator() ).
            parent( command.getParent() ).
            name( command.getName() ).
            icon( command.getIcon() ).
            rootDataSet( command.getDataSet() ).
            build();

        itemDao.createItem( createItemArgs );
        session.save();

        // TODO: index item or in dao?
    }
}
