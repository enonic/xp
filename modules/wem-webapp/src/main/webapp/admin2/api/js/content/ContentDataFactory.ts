module api_content {

    export class ContentDataFactory {

        public static createContentData(dataArray:api_data_json.DataJson[]):api_content.ContentData {

            var contentData = new api_content.ContentData();

            dataArray.forEach((dataJson:api_data_json.DataJson) => {
                if (dataJson.type == "DataSet") {
                    contentData.addData(api_data.DataFactory.createDataSet(<api_data_json.DataSetJson>dataJson));
                }
                else {
                    contentData.addData(api_data.DataFactory.createProperty(<api_data_json.PropertyJson>dataJson));
                }
            });
            return contentData;
        }

    }
}