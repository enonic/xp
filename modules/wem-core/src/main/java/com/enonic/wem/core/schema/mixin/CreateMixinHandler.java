package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;

public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle( final CommandContext context, final CreateMixin command )
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

        final Mixin.Builder mixinBuilder =
            newMixin().name( command.getName() ).formItems( command.getFormItems() ).displayName( command.getDisplayName() ).
                createdTime( currentTime ).modifiedTime( currentTime ).icon( command.getIcon() );

        final Mixin mixin = mixinBuilder.build();

        final Session session = context.getJcrSession();
        mixinDao.create( mixin, session );
        session.save();
        command.setResult( mixin.getQualifiedName() );

        // new way
        /*final ItemDao itemDao = new ItemJcrDao( context.getJcrSession() );
        final ItemPath parentItemPath = ItemPath.newPath( "/mixins" ).build();
        final UserKey creator = UserKey.superUser();
        final MixinItemTranslator translator = new MixinItemTranslator();
        final CreateItemArgs createItemArgs = newCreateItemArgs().
            creator( creator ).
            parent( parentItemPath ).
            name( command.getName() ).
            icon( command.getIcon() ).
            rootDataSet( translator.toRootDataSet( command ) ).
            build();

        itemDao.createItem( createItemArgs );

        final CreateItem createItemCommand = Commands.item().create().
            creator( creator ).
            name( command.getName() ).
            parent( parentItemPath ).
            dataSet( translator.toRootDataSet( command ) );

        final CreateItemResult createItemResult = context.getClient().execute( createItemCommand );
        final Mixin persistedMixin = MixinFactory.fromItem( createItemResult.getPersistedItem() );
        // TODO: set result*/
    }

    @Inject
    public void setMixinDao( final MixinDao value )
    {
        this.mixinDao = value;
    }
}
