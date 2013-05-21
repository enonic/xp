module API_content_data{

    export class DataSet extends Data {

        private dataById:{[s:string] : Data; } = {};

        constructor(name:string) {
            super(name);
        }

        dataCount(name:string):number {
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
            var index = this.dataCount(data.getName());
            data.setArrayIndex(index);
            var dataId = new DataId(data.getName(), index);
            var dataIdStr = dataId.toString();
            this.dataById[dataIdStr] = data;
        }

        getData(dataId:string):Data {
            return this.dataById[DataId.from(dataId).toString()];
        }

    }
}