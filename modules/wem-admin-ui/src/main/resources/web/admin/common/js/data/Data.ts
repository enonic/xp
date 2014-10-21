module api.data {

    export class Data implements api.Equitable, api.Cloneable {

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

        getParent(): DataSet {
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
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(this, DataSet),
                    "Expected Data to be a DataSet: " + api.ClassHelper.getClassName(this));
            return <DataSet>this;
        }

        toProperty(): Property {
            api.util.assert(api.ObjectHelper.iFrameSafeInstanceOf(this, Property),
                    "Expected Data to be a Property: " + api.ClassHelper.getClassName(this));
            return <Property>this;
        }

        toDataJson(): api.data.json.DataTypeWrapperJson {

            if (api.ObjectHelper.iFrameSafeInstanceOf(this, Property)) {
                return (<Property>this).toPropertyJson();
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(this, DataSet)) {
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

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Data)) {
                return false;
            }

            var other = <Data>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }
            if (!api.ObjectHelper.numberEquals(this.arrayIndex, other.arrayIndex)) {
                return false;
            }

            return true;
        }

        clone(): Data {

            throw new Error("Must be implemented by inheritors");
        }

        prettyPrint(indent?: string) {
            throw new Error("Must be implemented by inheritors");
        }
    }

}