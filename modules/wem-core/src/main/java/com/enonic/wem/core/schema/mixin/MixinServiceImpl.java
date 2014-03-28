package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.mixin.CreateMixinParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinParams;
import com.enonic.wem.api.command.schema.mixin.DeleteMixinResult;
import com.enonic.wem.api.command.schema.mixin.GetMixinParams;
import com.enonic.wem.api.command.schema.mixin.GetMixinsParams;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinParams;
import com.enonic.wem.api.command.schema.mixin.UpdateMixinResult;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

public class MixinServiceImpl
    implements MixinService
{
    @Inject
    private MixinDao mixinDao;
    @Inject
    private ContentTypeService contentTypeService;

    @Override
    public Mixin create( final CreateMixinParams params )
    {
        return new CreateMixinCommand().mixinDao( this.mixinDao ).params( params ).execute();
    }

    @Override
    public UpdateMixinResult update( final UpdateMixinParams params )
    {
        return new UpdateMixinCommand().mixinDao( this.mixinDao ).params( params ).execute();
    }

    @Override
    public DeleteMixinResult delete( final DeleteMixinParams params )
    {
        return new DeleteMixinCommand().mixinDao( this.mixinDao ).contentTypeService( this.contentTypeService ).params( params ).execute();
    }

    @Override
    public Mixin getByName( final GetMixinParams params )
    {
        return new GetMixinCommand().mixinDao( this.mixinDao ).params( params ).execute();
    }

    @Override
    public Mixins getByNames( final GetMixinsParams params )
    {
        return new GetMixinsCommand().mixinDao( this.mixinDao ).params( params ).execute();
    }

    @Override
    public Mixins getAll()
    {
        return mixinDao.getAllMixins();
    }
}
