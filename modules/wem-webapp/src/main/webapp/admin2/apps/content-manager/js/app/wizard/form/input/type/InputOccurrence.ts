module app_wizard_form_input_type {

    /*
     * Represents an occurrence or value of many. Translates to a Property in the data domain.
     */
    export class InputOccurrence {

        private inputOccurrences:InputOccurrences;

        private input:api_schema_content_form.Input;

        private index:number;

        constructor(inputOccurrences:InputOccurrences, index:number) {
            this.input = inputOccurrences.getInput();
            this.index = index;
            this.inputOccurrences = inputOccurrences;
        }

        setIndex(value:number) {
            this.index = value;
        }

        getIndex():number {
            return this.index;
        }

        getDataId():api_data.DataId {
            return new api_data.DataId(this.input.getName(), this.index);
        }

        showRemoveButton():boolean {

            if (this.oneAndOnly()) {
                return false;
            }
            else {
                return this.moreThanRequiredOccurrences();
            }
        }

        showAddButton():boolean {

            if( !this.isLastOccurrence() ) {
                return false;
            }

            return this.lessOccurrencesThanMaximumAllowed();
        }

        private oneAndOnly() {
            return this.index == 0 && this.inputOccurrences.countOccurrences() == 1;
        }

        private moreThanRequiredOccurrences() {
            return this.inputOccurrences.countOccurrences() > this.input.getOccurrences().getMinimum();
        }

        private lessOccurrencesThanMaximumAllowed():boolean {

            if( this.input.getOccurrences().getMaximum() == 0 )
            {
                return true;
            }
            return this.inputOccurrences.countOccurrences() < this.input.getOccurrences().getMaximum();
        }

        private isLastOccurrence() {
            return this.index == this.inputOccurrences.countOccurrences() -1;
        }
    }
}