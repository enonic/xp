package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


final class DeleteMixinCommand
{
    private MixinDao mixinDao;

    private DeleteMixinParams params;

    public DeleteMixinResult execute()
    {
        this.params.validate();

        return doExecute();
    }

    private DeleteMixinResult doExecute()
    {
        checkNotBeingUsed();

        final Mixin.Builder deletedMixin = mixinDao.getMixin( params.getName() );
        if ( deletedMixin == null )
        {
            throw new MixinNotFoundException( params.getName() );
        }

        mixinDao.deleteMixin( params.getName() );
        return new DeleteMixinResult( deletedMixin.build() );
    }

    private void checkNotBeingUsed()
    {
        final Mixin mixinToDelete = new GetMixinCommand().mixinDao( this.mixinDao ).params( new GetMixinParams( params.getName() ) ).execute();
        // TODO:[RYA]: Fix after ContentTypeService implement
/*
        final ContentTypes allContentTypes = context.getClient().execute( Commands.contentType().get().all() );
        final ContentTypes usingContentTypes = new MixinUsageResolver( mixinToDelete ).resolveUsingContentTypes( allContentTypes );
        if ( usingContentTypes.isNotEmpty() )
        {
            throw new UnableToDeleteMixinException( params.getName(), "Mixin is being used" );
        }
*/
    }

    public DeleteMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    public DeleteMixinCommand params( final DeleteMixinParams params )
    {
        this.params = params;
        return this;
    }
}
