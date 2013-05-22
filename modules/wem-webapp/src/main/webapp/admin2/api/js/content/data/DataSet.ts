module API_content_data{

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

        getData(dataId:string):Data {
            return this.dataById[DataId.from(dataId).toString()];
        }

    }
}