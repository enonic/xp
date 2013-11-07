module api_data {

    export class DataFactory {

        public static createRootDataSet(dataArray:api_data_json.DataJson[]):api_content.ContentData {

            var rootDataSet = new api_data.RootDataSet();

            if (dataArray != null) {
                dataArray.forEach((dataJson:api_data_json.DataJson) => {
                    if (dataJson.type == "DataSet") {
                        rootDataSet.addData(api_data.DataFactory.createDataSet(<api_data_json.DataSetJson>dataJson));
                    }
                    else {
                        rootDataSet.addData(api_data.DataFactory.createProperty(<api_data_json.PropertyJson>dataJson));
                    }
                });
            }
            return rootDataSet;
        }

        public static createDataSet(dataSetJson:api_data_json.DataSetJson):DataSet {

            var dataSet = new DataSet(dataSetJson.name);
            dataSetJson.value.forEach((dataJson:api_data_json.DataJson) => {

                if (dataJson.type == "DataSet") {
                    dataSet.addData(DataFactory.createDataSet(<api_data_json.DataSetJson>dataJson));
                }
                else {
                    dataSet.addData(DataFactory.createProperty(<api_data_json.PropertyJson>dataJson));
                }
            });
            return dataSet;
        }

        public static createProperty(propertyJson:api_data_json.PropertyJson):Property {

            return Property.fromJson(propertyJson);
        }
    }
}