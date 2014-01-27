package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.mixin.GetMixin;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class GetMixinHandler
    extends CommandHandler<GetMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final Mixin.Builder mixin = mixinDao.getMixin( command.getName() );
        if ( mixin == null )
        {
            if ( command.isNotFoundAsException() )
            {
                throw new MixinNotFoundException( command.getName() );
            }
            else
            {
                command.setResult( null );
            }
        }
        else
        {
            command.setResult( mixin.build() );
        }
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
