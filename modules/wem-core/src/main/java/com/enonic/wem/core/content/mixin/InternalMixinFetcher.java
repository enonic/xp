package com.enonic.wem.core.content.mixin;


import javax.jcr.Session;

import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.MixinFetcher;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.core.content.mixin.dao.MixinDao;

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
