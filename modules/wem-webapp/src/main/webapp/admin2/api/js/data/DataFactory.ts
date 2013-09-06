module api_data {

    export class DataFactory {

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

            return new Property(propertyJson.name, propertyJson.value, propertyJson.type);
        }
    }
}