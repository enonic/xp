module api_content {

    export class ContentDataFactory {

        public static createContentData(dataArray:api_data_json.DataTypeWrapperJson[]):api_content.ContentData {

            var contentData = new api_content.ContentData();

            if (dataArray != null) {
                dataArray.forEach((dataJson:api_data_json.DataTypeWrapperJson) => {
                    if (dataJson.DataSet) {
                        contentData.addData(api_data.DataFactory.createDataSet(dataJson.DataSet));
                    }
                    else {
                        contentData.addData(api_data.DataFactory.createProperty(dataJson.Property));
                    }
                });
            }
            return contentData;
        }

    }
}