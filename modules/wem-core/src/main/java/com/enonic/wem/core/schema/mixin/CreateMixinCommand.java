package com.enonic.wem.core.schema.mixin;

import java.time.Instant;

import com.enonic.wem.api.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinAlreadyExistException;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


final class CreateMixinCommand
{
    private MixinDao mixinDao;

    private CreateMixinParams params;

    public Mixin execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Mixin doExecute()
    {
        final Mixin existing = new GetMixinCommand().mixinDao( this.mixinDao ).params( new GetMixinParams( params.getName() ) ).execute();

        if ( existing != null )
        {
            throw new MixinAlreadyExistException( params.getName() );
        }

        final Mixin mixin = Mixin.newMixin().
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            icon( params.getSchemaIcon() ).
            formItems( params.getFormItems() ).
            createdTime( Instant.now() ).
            build();

        return mixinDao.createMixin( mixin );
    }

    CreateMixinCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    CreateMixinCommand params( final CreateMixinParams params )
    {
        this.params = params;
        return this;
    }
}
