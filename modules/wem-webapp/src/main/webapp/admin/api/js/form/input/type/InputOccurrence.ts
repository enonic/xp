module api_form_input_type {

    /*
     * Represents an occurrence or value of many. Translates to a Property in the data domain.
     */
    export class InputOccurrence extends api_form.FormItemOccurrence {

        constructor(inputOccurrences:InputOccurrences, index:number) {
            super(inputOccurrences, index, inputOccurrences.getInput().getOccurrences());
        }
    }
}