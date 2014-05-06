module api.form.inputtype.support {

    /*
     * Represents an occurrence or value of many. Translates to a Property in the data domain.
     */
    export class InputOccurrence extends api.form.FormItemOccurrence<InputOccurrenceView> {

        constructor(inputOccurrences:InputOccurrences, index:number) {
            super(inputOccurrences, index, inputOccurrences.getInput().getOccurrences());
        }
    }
}