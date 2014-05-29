module api.data {

    export class DataSet extends Data implements api.Equitable, api.Cloneable {

        private dataById: {[s:string] : Data;} = {};

        private dataArray: Data[] = [];

        constructor(name: string) {
            super(name);
        }

        nameCount(name: string): number {
            var count = 0;
            for (var i in this.dataById) {
                var data = this.dataById[i];
                if (data.getName() === name) {
                    count++;
                }
            }
            return count;
        }

        addData(data: Data) {
            data.setParent(this);
            var index = this.nameCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new DataId(data.getName(), index);
            this.dataById[dataId.toString()] = data;
            this.dataArray.push(data);
        }

        setData(dataArray: Data[]) {

            var newDataById: {[s:string] : Data;} = {};
            dataArray.forEach((data: Data, index: number) => {
                data.setParent(this);
                data.setArrayIndex(index);
                newDataById[data.getId().toString()] = data;
            });

            this.dataArray = dataArray;
            this.dataById = newDataById;
        }

        moveDataByName(name: string, index: number, destinationIndex: number) {
            this.dataArray.forEach((data: Data) => {
                if (data.getName() == name) {
                    if (data.getArrayIndex() == index) {
                        data.setArrayIndex(destinationIndex);
                    } else if (data.getArrayIndex() == destinationIndex) {
                        data.setArrayIndex(index);
                    }
                }
            });
        }

        removeData(idToRemove: DataId): Data {

            var dataToRemove = this.dataById[idToRemove.toString()];

            api.util.assertNotNull(dataToRemove, "Data to remove [" + idToRemove + "] not found in dataById");

            // Remove from map
            delete this.dataById[idToRemove.toString()];

            // Resolve index of Data to remove
            var indexToRemove = -1;
            this.dataArray.forEach((data: Data, index: number) => {
                if (data.getId().toString() == idToRemove.toString()) {
                    indexToRemove = index;
                }
            });
            api.util.assert(indexToRemove > -1, "Data to remove [" + idToRemove + "] not found in dataArray");

            // Remove Data from dataArray
            this.dataArray.splice(indexToRemove, 1);

            // Update the array index of the Data-s coming after...
            var dataArray = this.getDataByName(idToRemove.getName());

            for (var i = idToRemove.getArrayIndex(); i < dataArray.length; i++) {
                var data = dataArray[i];
                delete this.dataById[data.getId().toString()];
                data.setArrayIndex(i);
                this.dataById[data.getId().toString()] = data;
            }

            return dataToRemove;
        }

        getDataArray(): Data[] {
            var datas = [];
            this.dataArray.forEach((data: Data) => {
                datas.push(data);
            });
            return datas;
        }

        getData(dataId: string): Data {
            return this.getDataFromDataId(DataId.from(dataId));
        }

        getDataFromDataPath(path: DataPath): Data {

            if (path.elementCount() > 1) {
                return this.doForwardGetData(path);
            }
            else {
                return this.getDataFromDataId(path.getFirstElement().toDataId());
            }
        }

        private doForwardGetData(path: DataPath): Data {

            var data = this.getDataFromDataId(path.getFirstElement().toDataId());
            if (data == null) {
                return null;
            }

            return data.toDataSet().getDataFromDataPath(path.asNewWithoutFirstPathElement());
        }

        getDataFromDataId(dataId: DataId): Data {
            return this.dataById[dataId.toString()];
        }

        getDataByName(name: string): Data[] {

            var matches: Data[] = [];
            for (var i in this.dataById) {
                var data: Data = this.dataById[i];
                if (name === data.getName()) {
                    matches.push(data);
                }
            }

            return matches;
        }

        getProperty(path: string): Property {
            return this.getPropertyFromDataPath(DataPath.fromString(path));
        }

        getPropertyFromDataPath(path: DataPath): Property {
            var data = this.getDataFromDataPath(path);
            return data ? data.toProperty() : null;
        }

        getPropertyFromDataId(dataId: DataId): Property {
            var data = this.getDataFromDataId(dataId);
            return data ? data.toProperty() : null;
        }

        getPropertiesByName(name: string): Property[] {

            var matches: Property[] = [];
            this.getDataByName(name).forEach((data: Data) => {
                if (name === data.getName() && api.ObjectHelper.iFrameSafeInstanceOf(data, Property)) {
                    matches.push(<Property>data);
                }
                else if (name === data.getName() && !api.ObjectHelper.iFrameSafeInstanceOf(data, Property)) {
                    throw new Error("Expected data of type Property with name '" + name + "', got: " + data);
                }
            });
            return matches;
        }

        getDataSet(path: string): DataSet {
            return this.getDataSetFromDataPath(DataPath.fromString(path));
        }

        getDataSetFromDataPath(path: DataPath): DataSet {
            var data = this.getDataFromDataPath(path);
            if (data == null) {
                return null;
            }
            return data.toDataSet();
        }

        getDataSetFromDataId(dataId: DataId): DataSet {
            var data = this.getDataFromDataId(dataId);
            return data ? data.toDataSet() : null;
        }

        getDataSets(): DataSet[] {

            var dataSets: DataSet[] = [];
            for (var i in this.dataById) {
                var data: Data = this.dataById[i];
                if (api.ObjectHelper.iFrameSafeInstanceOf(data, DataSet)) {
                    dataSets.push(data.toDataSet());
                }
            }
            return dataSets;
        }

        getDataSetsByName(name: string): DataSet[] {

            var matches: DataSet[] = [];
            this.getDataByName(name).forEach((data: Data) => {
                if (name === data.getName() && api.ObjectHelper.iFrameSafeInstanceOf(data, DataSet)) {
                    matches.push(<DataSet>data);
                }
                else if (name === data.getName() && !api.ObjectHelper.iFrameSafeInstanceOf(data, DataSet)) {
                    throw new Error("Expected data of type DataSet with name '" + name + "', got: " + data);
                }
            });
            return matches;
        }

        toDataSetJson(): api.data.json.DataTypeWrapperJson {

            return <api.data.json.DataTypeWrapperJson>{ DataSet: <api.data.json.DataSetJson>{
                name: this.getName(),
                set: Data.datasToJson(this.getDataArray())
            }};
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DataSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <DataSet>o;

            if (!api.ObjectHelper.arrayEquals(this.dataArray, other.dataArray)) {
                return false;
            }

            return true;
        }

        clone(): DataSet {

            var clone = new DataSet(this.getName());
            clone.setArrayIndex(this.getArrayIndex());
            clone.setParent(this.getParent());

            this.dataArray.forEach((data: Data) => {
                var dataClone = data.clone();
                clone.dataArray.push(dataClone);
                clone.dataById[dataClone.getId().toString()] = data;
            });

            return clone;
        }

        prettyPrint(indent?: string) {
            var thisIndent = indent ? indent : "";

            console.log(thisIndent + this.getId().toString() + "{");

            this.getDataArray().forEach((data: Data) => {
                data.prettyPrint(thisIndent + "  ");
            });


            console.log(thisIndent + "}");
        }

    }
}