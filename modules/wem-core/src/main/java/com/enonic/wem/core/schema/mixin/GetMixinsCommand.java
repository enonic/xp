package com.enonic.wem.core.schema.mixin;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.schema.mixin.GetMixinsParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;
import com.enonic.wem.api.schema.mixin.MixinNotFoundException;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.schema.mixin.Mixins.newMixins;


final class GetMixinsCommand
{
    private MixinDao mixinDao;

    private GetMixinsParams params;

    Mixins execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Mixins doExecute()
    {
        final List<MixinName> missingMixins = new ArrayList<>();
        final Mixins mixins = getMixins( params.getNames(), missingMixins );

        if ( !missingMixins.isEmpty() )
        {
            throw new MixinNotFoundException( MixinNames.from( missingMixins ) );
        }

        return mixins;
    }

    private Mixins getMixins( final MixinNames mixinNames, final List<MixinName> missingMixins )
    {
        final Mixins.Builder mixins = newMixins();

        for ( MixinName mixinName : mixinNames )
        {
            final Mixin mixin = mixinDao.getMixin( mixinName );
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

    GetMixinsCommand mixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
        return this;
    }

    GetMixinsCommand params( final GetMixinsParams params )
    {
        this.params = params;
        return this;
    }
}
