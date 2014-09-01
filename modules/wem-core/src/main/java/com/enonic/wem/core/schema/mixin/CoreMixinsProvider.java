package com.enonic.wem.core.schema.mixin;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.SchemaProvider;
import com.enonic.wem.api.schema.Schemas;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.core.support.BaseCoreSchemaProvider;


public class CoreMixinsProvider
    extends BaseCoreSchemaProvider
    implements SchemaProvider

{
    private final MixinJsonSerializer serializer = new MixinJsonSerializer();

    private static final String[] DEMO_MIXINS = {"demo-mixin-address.json", "demo-mixin-norwegian-counties.json"};

    public CoreMixinsProvider()
    {
        super( "mixins" );
    }

    @Override
    public Schemas getSchemas()
    {
        List<Mixin> mixins = Lists.newArrayList();
        for ( String demoMixinFileName : DEMO_MIXINS )
        {
            final String mixinJson = loadFileAsString( demoMixinFileName );
            Mixin mixin = serializer.toMixin( mixinJson );
            mixin = Mixin.newMixin( mixin ).
                icon( loadSchemaIcon( mixin.getName().toString() ) ).
                build();

            mixins.add( mixin );
        }
        return Schemas.from( mixins );
    }

}
