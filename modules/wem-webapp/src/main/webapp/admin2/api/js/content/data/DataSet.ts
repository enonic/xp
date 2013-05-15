module API.content.data{

    export class DataSet extends Data {

        private dataById = new Object();

        constructor(json) {
            super(json.name);
        }

        addData( data:Data ) {
            if(  )
            this.dataById[data.getName()] = data;
        }

        getData(dataId:string):Data{
            return this.dataById[dataId];
        }

    }
}