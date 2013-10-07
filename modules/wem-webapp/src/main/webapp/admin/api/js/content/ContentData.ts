module api_content{

    export class ContentData extends api_data.DataSet {

        constructor() {
            super("");
        }

        toJson():api_data_json.DataJson[] {

            var dataArray:api_data_json.DataJson[] = [];

            this.getDataArray().forEach((data:api_data.Data) => {
                dataArray.push(data.toDataJson());
            });
            return dataArray;
        }
    }
}