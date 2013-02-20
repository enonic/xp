package com.enonic.wem.core.content.schema.mixin;


import javax.jcr.Session;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MixinFetcher;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

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
        mixinDao.select( QualifiedMixinNames.from( qualifiedName ), session );
        return null;
    }
}
