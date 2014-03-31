module api.data {

    export class Data {

        private name: string;

        private arrayIndex: number;

        private parent: DataSet;

        constructor(name: string) {
            api.util.assertNotNull(name, "name of a Data cannot be null");
            this.name = name;
        }

        setArrayIndex(value: number) {
            this.arrayIndex = value;
        }

        public setParent(parent: DataSet) {
            this.parent = parent;
        }

        getId(): DataId {
            return new DataId(this.name, this.arrayIndex);
        }

        getName(): string {
            return this.name;
        }

        getParent(): Data {
            return this.parent;
        }

        getParentPath(): DataPath {

            var parent = this.getParent();
            var parentPath: DataPath;
            if (parent) {
                parentPath = parent.getPath();
            }
            else {
                parentPath = DataPath.ROOT;
            }
            return parentPath;
        }

        getPath(): DataPath {

            var parentPath = this.getParentPath();
            var element = new DataPathElement(this.getName(), this.getArrayIndex());
            var path = DataPath.fromParent(parentPath, element);
            return path;
        }

        getArrayIndex(): number {
            return this.arrayIndex;
        }

        toDataSet(): DataSet {
            api.util.assert(this instanceof DataSet, "Expected Data to be a DataSet: " + api.util.getClassName(this));
            return <DataSet>this;
        }

        toProperty(): Property {
            api.util.assert(this instanceof Property, "Expected Data to be a Property: " + api.util.getClassName(this));
            return <Property>this;
        }

        toDataJson(): api.data.json.DataTypeWrapperJson {

            if (this instanceof Property) {
                return (<Property>this).toPropertyJson();
            }
            else if (this instanceof DataSet) {
                return (<DataSet>this).toDataSetJson();
            }
            else {
                throw new Error("Unsupported data: " + this);
            }
        }

        static datasToJson(datas: Data[]): api.data.json.DataTypeWrapperJson[] {
            var array: api.data.json.DataTypeWrapperJson[] = [];
            datas.forEach((data: Data) => {
                array.push(data.toDataJson());
            });
            return array;
        }

        equals(data: Data): boolean {
            return this.name == data.getName() && this.arrayIndex == data.getArrayIndex();
        }
    }

}