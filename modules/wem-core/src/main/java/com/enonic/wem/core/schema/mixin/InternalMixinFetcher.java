package com.enonic.wem.core.schema.mixin;


import javax.jcr.Session;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinFetcher;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

public class InternalMixinFetcher
    implements MixinFetcher
{
    private final MixinDao mixinDao;

    private final Session session;

    public InternalMixinFetcher( final MixinDao mixinDao, final Session session )
    {
        this.mixinDao = mixinDao;
        this.session = session;
    }

    @Override
    public Mixin getMixin( final QualifiedMixinName qualifiedName )
    {
        return mixinDao.select( QualifiedMixinNames.from( qualifiedName ), session ).first();
    }
}
