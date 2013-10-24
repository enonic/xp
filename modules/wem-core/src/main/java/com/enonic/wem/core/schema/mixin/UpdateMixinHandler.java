package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;
import javax.jcr.Session;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.schema.mixin.editor.MixinEditor;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;


public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final QualifiedMixinName qualifiedMixinName = command.getQualifiedName();
        final MixinEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        final Mixin mixin = retrieveMixin( session, qualifiedMixinName );
        if ( mixin == null )
        {
            command.setResult( UpdateMixinResult.NOT_FOUND );
        }
        else
        {
            final Mixin modifiedMixin = editor.edit( mixin );
            if ( modifiedMixin != null )
            {
                updateMixin( session, modifiedMixin );
                session.save();
                command.setResult( UpdateMixinResult.SUCCESS );
            }
            command.setResult( UpdateMixinResult.SUCCESS );

            /*final ItemDao itemDao = new ItemJcrDao( context.getJcrSession() );
            final UserKey updater = UserKey.superUser();
            final MixinDataSetTranslator translator = new MixinDataSetTranslator();
            final UpdateItemArgs updateItemArgs = newUpdateItemArgs().
                updater( updater ).
                itemToUpdate( new ItemId( mixin.getId() ) ).
                icon( mixin.getIcon() ).
                rootDataSet( translator.toRootDataSet( mixin ) ).
                build();

            itemDao.updateItem( updateItemArgs );

            final SetItemEditor itemEditor = SetItemEditor.newSetItemEditor().
                name( mixin.getName() ).
                icon( mixin.getIcon() ).
                rootDataSet( translator.toRootDataSet( mixin ) ).
                build();

            final UpdateItem updateCommand = Commands.item().update().
                modifier( updater ).
                itemToUpdate( new ItemId( mixin.getId() ) ).
                readAt( editor.getReadAt() ).
                editor( itemEditor );

            context.getClient().execute( updateCommand );*/
        }
    }

    private void updateMixin( final Session session, final Mixin mixin )
    {
        final Mixin mixinModified = newMixin( mixin ).modifiedTime( DateTime.now() ).build();
        mixinDao.update( mixinModified, session );
    }

    private Mixin retrieveMixin( final Session session, final QualifiedMixinName qualifiedMixinName )
    {
        final Mixins mixins = mixinDao.select( QualifiedMixinNames.from( qualifiedMixinName ), session );
        return mixins.first();
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
