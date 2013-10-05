package com.enonic.wem.core.schema.mixin;


import com.enonic.wem.api.command.schema.mixin.CreateMixin;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

class MixinItemTranslator
{
    RootDataSet toRootDataSet( final CreateMixin createMixin )
    {
        final RootDataSet dataSet = new RootDataSet();
        dataSet.setProperty( "displayName", new Value.Text( createMixin.getDisplayName() ) );

        // TODO: Uncomment when change from CMS-2221 Create serializer for serializing form items to data
        // TODO: is ready
        //DataSet formItems = new DataSet("formItems");
        //List<Data> dataList = SerializerForFormItemToData.formItemsToData( createMixin.getFormItems() );
        //for( Data data : dataList ) {
        //    dataSet.add( data );
        //}

        return dataSet;
    }
}
