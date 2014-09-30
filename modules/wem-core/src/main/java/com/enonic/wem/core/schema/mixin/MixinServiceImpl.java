package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;

import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.GetMixinsParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

public class MixinServiceImpl
    implements MixinService
{
    @Inject
    private MixinDao mixinDao;

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

    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
