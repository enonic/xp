package com.enonic.wem.core.content.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.content.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;


public final class DeleteMixinHandler
    extends CommandHandler<DeleteMixin>
{
    private MixinDao mixinDao;

    public DeleteMixinHandler()
    {
        super( DeleteMixin.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteMixin command )
        throws Exception
    {
        final QualifiedMixinName qualifiedMixinName = command.getName();
        try
        {
            /* TODO: if ( contentTypeDao.countMixinUsage( qualifiedMixinName, context.getJcrSession() ) > 0 )
            {
                command.setResult( DeleteMixinResult.UNABLE_TO_DELETE );
            }
            else
            {*/
            mixinDao.delete( qualifiedMixinName, context.getJcrSession() );
            context.getJcrSession().save();
            command.setResult( DeleteMixinResult.SUCCESS );
            //}
        }
        catch ( MixinNotFoundException e )
        {
            command.setResult( DeleteMixinResult.NOT_FOUND );
        }
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
