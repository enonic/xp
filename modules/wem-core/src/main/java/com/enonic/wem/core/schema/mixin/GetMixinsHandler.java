package com.enonic.wem.core.schema.mixin;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;


public final class GetMixinsHandler
    extends CommandHandler<GetMixins>
{
    private MixinDao mixinDao;

    @Override
    public void handle( final GetMixins command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        // TODO: final ItemDao itemDao = new ItemJcrDao( session );

        final Mixins mixins;
        if ( command.isGetAll() )
        {
            // TODO: itemDao.getAllOfType();
            mixins = getAllMixins( session );
        }
        else
        {
            final QualifiedMixinNames qualifiedNames = command.getQualifiedMixinNames();
            mixins = getMixins( qualifiedNames, session );
        }

        command.setResult( mixins );
    }

    private Mixins getAllMixins( final Session session )
    {
        return mixinDao.selectAll( session );
    }

    private Mixins getMixins( final QualifiedMixinNames qualifiedMixinNames, final Session session )
    {
        return mixinDao.select( qualifiedMixinNames, session );
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
