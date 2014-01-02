module api.content {

    export class ContentDataFactory {

        public static createContentData(dataArray:api.data.json.DataTypeWrapperJson[]):api.content.ContentData {

            var contentData = new api.content.ContentData();

            if (dataArray != null) {
                dataArray.forEach((dataJson:api.data.json.DataTypeWrapperJson) => {
                    if (dataJson.DataSet) {
                        contentData.addData(api.data.DataFactory.createDataSet(dataJson.DataSet));
                    }
                    else {
                        contentData.addData(api.data.DataFactory.createProperty(dataJson.Property));
                    }
                });
            }
            return contentData;
        }

    }
}