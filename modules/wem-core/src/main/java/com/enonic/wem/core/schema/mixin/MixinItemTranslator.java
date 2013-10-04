package com.enonic.wem.core.schema.mixin;


import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.mixin.Mixin;

class MixinItemTranslator
{
    RootDataSet toRootDataSet( final CreateMixin createMixin )
    {
        final RootDataSet dataSet = new RootDataSet();
        dataSet.setProperty( "displayName", new Value.Text( createMixin.getDisplayName() ) );
        // TODO: formItems
        return dataSet;
    }

    public RootDataSet toRootDataSet( final Mixin mixin )
    {
        final RootDataSet dataSet = new RootDataSet();
        dataSet.setProperty( "displayName", new Value.Text( mixin.getDisplayName() ) );
        // TODO: formItems
        return dataSet;
    }
}
