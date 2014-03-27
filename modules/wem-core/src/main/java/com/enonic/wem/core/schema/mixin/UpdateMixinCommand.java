package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinAlreadyExistException;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


final class UpdateMixinCommand
{
    private MixinDao mixinDao;

    private UpdateMixinParams params;

    UpdateMixinResult execute()
    {
        this.params.validate();

        return doExecute();
    }

    private UpdateMixinResult doExecute()
    {
        final Mixin original = new GetMixinCommand().mixinDao( this.mixinDao ).params( new GetMixinParams( params.getName() ) ).execute();
        if ( original == null )
        {
            throw new MixinNotFoundException( params.getName() );
        }

        final Mixin modifiedMixin = params.getEditor().edit( original );
        if ( modifiedMixin != null )
        {
            if ( !original.getName().equals( modifiedMixin.getName() ) )
            {
                final Mixin existing = new GetMixinCommand()
                    .mixinDao( this.mixinDao )
                    .params( new GetMixinParams( modifiedMixin.getName() ) )
                    .execute();
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

            return new UpdateMixinResult( modifiedMixin );
        }
        else
        {
            return new UpdateMixinResult( original );
        }
    }

    UpdateMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    UpdateMixinCommand params( final UpdateMixinParams params )
    {
        this.params = params;
        return this;
    }
}
