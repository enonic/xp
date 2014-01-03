module api.data {

    export class DataFactory {

        public static createRootDataSet( dataArray:api.data.json.DataTypeWrapperJson[] ):api.data.RootDataSet {

            var rootDataSet = new api.data.RootDataSet();

            if ( dataArray != null ) {
                dataArray.forEach( ( dataJson:api.data.json.DataTypeWrapperJson ) => {
                                       if ( dataJson.DataSet ) {
                                           rootDataSet.addData( api.data.DataFactory.createDataSet( dataJson.DataSet ) );
                                       }
                                       else {
                                           rootDataSet.addData( api.data.DataFactory.createProperty( dataJson.Property ) );
                                       }
                                   } );
            }
            return rootDataSet;
        }

        public static createDataSet( dataSetJson:api.data.json.DataSetJson ):DataSet {

            var dataSet = new DataSet( dataSetJson.name );
            dataSetJson.set.forEach( ( dataJson:api.data.json.DataTypeWrapperJson ) => {

                                         if ( dataJson.DataSet ) {
                                             dataSet.addData( api.data.DataFactory.createDataSet( dataJson.DataSet ) );
                                         }
                                         else {
                                             dataSet.addData( api.data.DataFactory.createProperty( dataJson.Property ) );
                                         }
                                     } );
            return dataSet;
        }

        public static createProperty( propertyJson:api.data.json.PropertyJson ):Property {

            return Property.fromJson( propertyJson );
        }
    }
}