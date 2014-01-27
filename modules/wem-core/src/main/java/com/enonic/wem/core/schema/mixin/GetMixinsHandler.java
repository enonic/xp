package com.enonic.wem.core.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.enonic.wem.api.command.schema.mixin.GetMixins;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.mixin.Mixins.newMixins;


public final class GetMixinsHandler
    extends CommandHandler<GetMixins>
{
    private MixinDao mixinDao;

    @Override
    public void handle()
        throws Exception
    {
        final Mixins mixins;

        if ( command.isGetAll() )
        {
            mixins = mixinDao.getAllMixins();
        }
        else
        {
            final List<MixinName> missingMixins = new ArrayList<>();
            mixins = getMixins( command.getNames(), missingMixins );

            if ( !missingMixins.isEmpty() )
            {
                throw new MixinNotFoundException( MixinNames.from( missingMixins ) );
            }
        }

        command.setResult( mixins );
    }

    private Mixins getMixins( final MixinNames mixinNames, final List<MixinName> missingMixins )
    {
        final Mixins.Builder mixins = newMixins();

        for ( MixinName mixinName : mixinNames )
        {
            final Mixin mixin = getMixin( mixinName );
            if ( mixin != null )
            {
                mixins.add( mixin );
            }
            else
            {
                missingMixins.add( mixinName );
            }
        }
        return mixins.build();
    }

    private Mixin getMixin( final MixinName mixinName )
    {
        final Mixin.Builder mixin = mixinDao.getMixin( mixinName );
        return mixin != null ? mixin.build() : null;
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
