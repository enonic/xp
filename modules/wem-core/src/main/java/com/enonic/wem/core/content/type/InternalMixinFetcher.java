package com.enonic.wem.core.content.type;


import javax.jcr.Session;

import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.MixinFetcher;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.core.content.type.dao.MixinDao;

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
        mixinDao.retrieveMixins( QualifiedMixinNames.from( qualifiedName ), session );
        return null;
    }
}
