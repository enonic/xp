package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.mixin.DeleteMixin;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class DeleteMixinHandler
    extends CommandHandler<DeleteMixin>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        checkNotBeingUsed();

        final Mixin.Builder deletedMixin = mixinDao.getMixin( command.getName() );
        if ( deletedMixin == null )
        {
            throw new MixinNotFoundException( command.getName() );
        }

        mixinDao.deleteMixin( command.getName() );
        command.setResult( new DeleteMixinResult( deletedMixin.build() ) );
    }

    private void checkNotBeingUsed()
    {
        final Mixin mixinToDelete = context.getClient().execute( Commands.mixin().get().byName( command.getName() ) );
        final ContentTypes allContentTypes = context.getClient().execute( Commands.contentType().get().all() );
        final ContentTypes usingContentTypes = new MixinUsageResolver( mixinToDelete ).resolveUsingContentTypes( allContentTypes );
        if ( usingContentTypes.isNotEmpty() )
        {
            throw new UnableToDeleteMixinException( command.getName(), "Mixin is being used" );
        }
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
