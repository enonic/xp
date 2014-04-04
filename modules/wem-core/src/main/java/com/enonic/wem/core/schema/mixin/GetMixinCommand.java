package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


final class GetMixinCommand
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

    GetMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    GetMixinCommand params( final GetMixinParams params )
    {
        this.params = params;
        return this;
    }
}
