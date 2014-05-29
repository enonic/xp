module api.data {

    export class RootDataSet extends api.data.DataSet implements api.Equitable, api.Cloneable {

        constructor() {
            super("");
        }

        getParent(): DataSet {
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

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, RootDataSet)) {
                return false;
            }

            return super.equals(o);
        }

        clone(): RootDataSet {

            var clone = new RootDataSet();
            clone.setArrayIndex(this.getArrayIndex());
            clone.setParent(this.getParent());
            this.getDataArray().forEach((data: Data) => {
                clone.addData(data.clone());
            });
            return clone;
        }

        prettyPrint(indent?: string) {

            var thisIndent = indent ? indent : "";
            console.log(thisIndent + "{");

            this.getDataArray().forEach((data: Data) => {
                data.prettyPrint(thisIndent + "  ");
            });

            console.log(thisIndent + "}");
        }
    }
}