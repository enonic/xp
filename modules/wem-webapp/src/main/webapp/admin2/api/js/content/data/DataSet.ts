module API.content.data{

    export class DataSet extends Data {

        private dataById;

        constructor(json) {
            super(json.name);
        }

        addData( data:Data )
        {
            this.dataById.push( data.getName(), data );
        }
    }
}