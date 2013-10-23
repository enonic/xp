module api_form{


    export class Occurrences {

        private minimum:number;
        private maximum:number;

        constructor(json) {
            this.minimum = json.minimum;
            this.maximum = json.maximum;
        }

        getMaximum():number {
            return this.maximum;
        }

        getMinimum():number {
            return this.minimum;
        }

        required():boolean {
            return this.minimum > 0;
        }

        minimumReached(occurrenceCount:number) {
            return occurrenceCount > this.minimum;
        }

        maximumReached(occurrenceCount:number):boolean {
            if (this.maximum == 0) {
                return false;
            }
            return occurrenceCount >= this.maximum;
        }

        public toJson():api_form_json.OccurrencesJson {

            return <api_form_json.OccurrencesJson>{
                maximum : this.getMaximum(),
                minimum : this.getMinimum()
            };
        }
    }
}