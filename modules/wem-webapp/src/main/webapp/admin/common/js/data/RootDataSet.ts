module api.data{

    export class RootDataSet extends api.data.DataSet {

        constructor() {
            super("");
        }

        toJson():api.data.json.DataTypeWrapperJson[] {

            var dataArray:api.data.json.DataTypeWrapperJson[] = [];

            this.getDataArray().forEach((data:api.data.Data) => {
                dataArray.push(data.toDataJson());
            });
            return dataArray;
        }
    }
}