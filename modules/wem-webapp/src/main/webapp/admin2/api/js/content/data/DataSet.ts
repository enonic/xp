module API.content.data{

    export class DataSet extends Data {

        private dataById = {};

        constructor(name:string) {
            super(name);
        }

        /*private dataCount(name:string) {
            var count = 0;
            for (var i in dataById) {
                var data = dataById[i];
                if (data.getName() === name) {
                    count++;
                }
            }
            return count;
        }*/

        addData(data:Data) {
            data.setParent(this);
            //data.setArrayIndex(this.dataCount(data.getName()));
            this.dataById[data.getName()] = data;
        }

        getData(dataId:string):Data {
            return this.dataById[dataId];
        }

    }
}