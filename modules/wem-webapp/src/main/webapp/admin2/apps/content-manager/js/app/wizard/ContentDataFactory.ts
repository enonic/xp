module app_wizard {

    export class ContentDataFactory {

        public static createContentData(remoteData:api_remote_content.Data[]):api_content_data.ContentData {

            var contentData = new api_content_data.ContentData();

            remoteData.forEach((data:api_remote_content.Data) => {
                if (data.type == "DataSet") {
                    contentData.addData(ContentDataFactory.createDataSet(<api_remote_content.DataSet>data));
                }
                else {
                    contentData.addData(ContentDataFactory.createProperty(<api_remote_content.Property>data));
                }
            });
            return contentData;
        }

        public static createDataSet(remote:api_remote_content.DataSet):api_data.DataSet {

            var dataSet = new api_data.DataSet(remote.name);
            remote.value.forEach((data:api_remote_content.Data) => {

                if (data.type == "DataSet") {
                    dataSet.addData(ContentDataFactory.createDataSet(<api_remote_content.DataSet>data));
                }
                else {
                    dataSet.addData(ContentDataFactory.createProperty(<api_remote_content.Property>data));
                }
            });
            return dataSet;
        }

        public static createProperty(remote:api_remote_content.Property):api_data.Property {

            return new api_data.Property(remote.name, remote.value, remote.type);
        }
    }
}