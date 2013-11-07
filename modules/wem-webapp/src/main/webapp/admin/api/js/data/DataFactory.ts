module api_data {

    export class DataFactory {

        public static createRootDataSet(dataArray:api_data_json.DataTypeWrapperJson[]):api_content.ContentData {

            var rootDataSet = new api_data.RootDataSet();

            if (dataArray != null) {
                dataArray.forEach((dataJson:api_data_json.DataTypeWrapperJson) => {
                    if (dataJson.DataSet) {
                        rootDataSet.addData(api_data.DataFactory.createDataSet(dataJson.DataSet));
                    }
                    else {
                        rootDataSet.addData(api_data.DataFactory.createProperty(dataJson.Property));
                    }
                });
            }
            return rootDataSet;
        }

        public static createDataSet(dataSetJson:api_data_json.DataSetJson):DataSet {

            var dataSet = new DataSet(dataSetJson.name);
            dataSetJson.value.forEach((dataJson:api_data_json.DataTypeWrapperJson) => {

                if (dataJson.DataSet) {
                    dataSet.addData(api_data.DataFactory.createDataSet(dataJson.DataSet));
                }
                else {
                    dataSet.addData(api_data.DataFactory.createProperty(dataJson.Property));
                }
            });
            return dataSet;
        }

        public static createProperty(propertyJson:api_data_json.PropertyJson):Property {

            return Property.fromJson(propertyJson);
        }
    }
}