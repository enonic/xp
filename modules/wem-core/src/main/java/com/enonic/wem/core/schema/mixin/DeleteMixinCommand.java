package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.content.GetAllContentTypesParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.UnableToDeleteMixinException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


final class DeleteMixinCommand
{
    private MixinDao mixinDao;

    private ContentTypeService contentTypeService;

    private DeleteMixinParams params;

    DeleteMixinResult execute()
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
        final ContentTypes allContentTypes = contentTypeService.getAll( new GetAllContentTypesParams() );
        final ContentTypes usingContentTypes = new MixinUsageResolver( mixinToDelete ).resolveUsingContentTypes( allContentTypes );
        if ( usingContentTypes.isNotEmpty() )
        {
            throw new UnableToDeleteMixinException( params.getName(), "Mixin is being used" );
        }
    }

    DeleteMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    DeleteMixinCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }

    DeleteMixinCommand params( final DeleteMixinParams params )
    {
        this.params = params;
        return this;
    }
}
