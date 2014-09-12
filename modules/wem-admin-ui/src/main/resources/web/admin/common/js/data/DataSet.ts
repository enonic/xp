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

        addProperty(name: string, value: Value): Property {
            var property = new Property(name, value);
            this.addData(property);
            return property;
        }

        setProperty(dataId: DataId, value: Value): Property {
            var existing = this.getPropertyById(dataId);
            existing.setValue(value)
            return existing;
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

        getDataByPath(path: DataPath): Data {

            if (path.elementCount() > 1) {
                return this.doForwardGetData(path);
            }
            else {
                return this.getDataById(path.getFirstElement().toDataId());
            }
        }

        private doForwardGetData(path: DataPath): Data {

            var data = this.getDataById(path.getFirstElement().toDataId());
            if (data == null) {
                return null;
            }

            return data.toDataSet().getDataByPath(path.asNewWithoutFirstPathElement());
        }

        getDataById(dataId: DataId): Data {
            return this.dataById[dataId.toString()];
        }

        getData(identifier: any): Data {
            if (typeof identifier === 'string') {
                return this.getDataByPath(DataPath.fromString(<string>identifier));
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataPath)) {
                return this.getDataByPath(<api.data.DataPath>identifier);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataId)) {
                return this.getDataById(<api.data.DataId>identifier);
            }
            else {
                return this.getDataByPath(DataPath.fromString(identifier.toString()));
            }
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

        getProperty(identifier: any): Property {
            if (typeof identifier === 'string') {
                return this.getPropertyByPath(DataPath.fromString(<string>identifier));
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataPath)) {
                return this.getPropertyByPath(<api.data.DataPath>identifier);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataId)) {
                return this.getPropertyById(<api.data.DataId>identifier);
            }
            else {
                return this.getPropertyByPath(DataPath.fromString(identifier.toString()));
            }
        }

        getPropertyByPath(path: DataPath): Property {
            var data = this.getDataByPath(path);
            return data ? data.toProperty() : null;
        }

        getPropertyById(dataId: DataId): Property {
            var data = this.getDataById(dataId);
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

        getDataSet(identifier: any): DataSet {
            if (typeof identifier === 'string') {
                return this.getDataSetByPath(DataPath.fromString(<string>identifier));
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataPath)) {
                return this.getDataSetByPath(<api.data.DataPath>identifier);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(identifier, api.data.DataId)) {
                return this.getDataSetById(<api.data.DataId>identifier);
            }
            else {
                return this.getDataSetByPath(DataPath.fromString(identifier.toString()));
            }
        }

        getDataSetByPath(path: DataPath): DataSet {
            var data = this.getDataByPath(path);
            if (data == null) {
                return null;
            }
            return data.toDataSet();
        }

        getDataSetById(dataId: DataId): DataSet {
            var data = this.getDataById(dataId);
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
                clone.dataById[dataClone.getId().toString()] = dataClone;
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