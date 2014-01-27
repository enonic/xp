package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinAlreadyExistException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class CreateMixinHandler
    extends CommandHandler<CreateMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final Mixin existing = context.getClient().execute( Commands.mixin().get().byName( command.getName() ) );
        if ( existing != null )
        {
            throw new MixinAlreadyExistException( command.getName() );
        }

        final Mixin mixin = Mixin.newMixin().
            name( command.getName() ).
            displayName( command.getDisplayName() ).
            schemaIcon( command.getSchemaIcon() ).
            formItems( command.getFormItems() ).
            createdTime( DateTime.now() ).
            //creator( ... ).
                build();

        final Mixin createdMixin = mixinDao.createMixin( mixin );
        command.setResult( createdMixin );
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
