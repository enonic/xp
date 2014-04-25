module api.data {

    export class RootDataSet extends api.data.DataSet implements api.Equitable {

        constructor() {
            super("");
        }

        getParent(): Data {
            return null;
        }

        getParentPath(): DataPath {

            return null;
        }

        getPath(): DataPath {

            return DataPath.ROOT;
        }

        toJson(): api.data.json.DataTypeWrapperJson[] {

            var dataArray: api.data.json.DataTypeWrapperJson[] = [];

            this.getDataArray().forEach((data: api.data.Data) => {
                dataArray.push(data.toDataJson());
            });
            return dataArray;
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof RootDataSet)) {
                return false;
            }

            return super.equals(o);
        }
    }
}