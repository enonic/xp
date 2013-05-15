module API.content.schema.content.form{


    export class Occurrences   {

        private minimum:number;
        private maximum:number;

        constructor( json )
        {
            this.minimum = json.minimum;
            this.maximum = json.maximum;
        }
    }
}