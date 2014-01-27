package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.UpdateMixin;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinAlreadyExistException;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class UpdateMixinHandler
    extends CommandHandler<UpdateMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final Mixin original = context.getClient().execute( Commands.mixin().get().byName( command.getName() ) );
        if ( original == null )
        {
            throw new MixinNotFoundException( command.getName() );
        }

        final Mixin modifiedMixin = command.getEditor().edit( original );
        if ( modifiedMixin != null )
        {
            if ( !original.getName().equals( modifiedMixin.getName() ) )
            {
                final Mixin existing = context.getClient().execute( Commands.mixin().get().byName( modifiedMixin.getName() ) );
                if ( existing != null )
                {
                    throw new MixinAlreadyExistException( modifiedMixin.getName() );
                }

                mixinDao.updateMixin( modifiedMixin );
                mixinDao.deleteMixin( original.getName() );
            }
            else
            {
                mixinDao.updateMixin( modifiedMixin );
            }

            command.setResult( new UpdateMixinResult( modifiedMixin ) );
        }
        else
        {
            command.setResult( new UpdateMixinResult( original ) );
        }
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
