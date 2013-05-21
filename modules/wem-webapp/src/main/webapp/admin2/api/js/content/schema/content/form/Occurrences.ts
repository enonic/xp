module API_content_schema_content_form{


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