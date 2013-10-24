package com.enonic.wem.core.schema.mixin;


import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.support.SerializerForFormItemToData;

class MixinDataSetTranslator
{
    RootDataSet toRootDataSet( final CreateMixin createMixin )
    {
        final RootDataSet dataSet = new RootDataSet();
        dataSet.setProperty( "displayName", new Value.String( createMixin.getDisplayName() ) );

        final DataSet formItems = new DataSet( "formItems" );
        for ( Data data : new SerializerForFormItemToData().serializeFormItems( createMixin.getFormItems() ) )
        {
            dataSet.add( data );
        }
        dataSet.add( formItems );
        return dataSet;
    }
}
