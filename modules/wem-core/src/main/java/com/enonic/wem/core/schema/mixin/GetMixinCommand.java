package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class GetMixinCommand
{
    private MixinDao mixinDao;

    private GetMixinParams params;

    public Mixin execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Mixin doExecute()
    {
        final Mixin.Builder mixin = mixinDao.getMixin( params.getName() );
        if ( mixin == null )
        {
            if ( params.isNotFoundAsException() )
            {
                throw new MixinNotFoundException( params.getName() );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return mixin.build();
        }
    }

    public GetMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    public GetMixinCommand params( final GetMixinParams params )
    {
        this.params = params;
        return this;
    }
}
