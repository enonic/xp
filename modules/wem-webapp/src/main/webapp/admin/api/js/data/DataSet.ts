module api_data{

    export class DataSet extends Data {

        private dataById:{[s:string] : Data; } = {};

        constructor(name:string) {
            super(name);
        }

        nameCount(name:string):number {
            var count = 0;
            for (var i in this.dataById) {
                var data = this.dataById[i];
                if (data.getName() === name) {
                    count++;
                }
            }
            return count;
        }

        addData(data:Data) {
            data.setParent(this);
            var index = this.nameCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new DataId(data.getName(), index);
            this.dataById[dataId.toString()] = data;
        }

        getDataArray():Data[] {
            var datas = [];
            for (var i in this.dataById) {
                var data = this.dataById[i];
                datas.push(data);
            }
            return datas;
        }

        getData(dataId:string):Data {
            return this.dataById[DataId.from(dataId).toString()];
        }

        getDataByName(name:string):Data[] {

            var matches:Data[] = [];
            for (var i in this.dataById) {
                var data:Data = this.dataById[i];
                if (name === data.getName()) {
                    matches.push(data);
                }
            }

            return matches;
        }

        getPropertiesByName(name:string):Property[] {

            var matches:Property[] = [];
            this.getDataByName(name).forEach((data:Data) => {
                if (name === data.getName() && data instanceof Property) {
                    matches.push(<Property>data);
                }
                else if (name === data.getName() && !(data instanceof Property)) {
                    throw new Error("Expected data of type Property with name '" + name + "', got: " + data);
                }
            });
            return matches;
        }

        getDataSetsByName(name:string):DataSet[] {

            var matches:DataSet[] = [];
            this.getDataByName(name).forEach((data:Data) => {
                if (name === data.getName() && data instanceof DataSet) {
                    matches.push(<DataSet>data);
                }
                else if (name === data.getName() && !(data instanceof DataSet)) {
                    throw new Error("Expected data of type DataSet with name '" + name + "', got: " + data);
                }
            });
            return matches;
        }

        toDataSetJson():api_data_json.DataSetJson {

            var dataArray:api_data_json.DataJson[] = [];

            this.getDataArray().forEach((data:api_data.Data) => {
                dataArray.push(data.toDataJson());
            });

            return <api_data_json.DataSetJson>{
                name: this.getName(),
                type: "DataSet",
                value: dataArray
            };
        }

    }
}