module api_schema_content_form{


    export class Occurrences   {

        private minimum:number;
        private maximum:number;

        constructor( json )
        {
            this.minimum = json.minimum;
            this.maximum = json.maximum;
        }

        getMaximum():number {
            return this.maximum;
        }

        getMinimum():number {
            return this.minimum;
        }
    }
}