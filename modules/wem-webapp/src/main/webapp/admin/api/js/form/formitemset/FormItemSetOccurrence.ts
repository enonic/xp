module api_form_formitemset {

    /*
     * Represents an occurrence of many. Translates to a DataSet in the data domain.
     */
    export class FormItemSetOccurrence extends api_form.FormItemOccurrence<FormItemSetOccurrenceView> {

        constructor(formItemSetOccurrences:FormItemSetOccurrences, index:number) {
            super(formItemSetOccurrences, index, formItemSetOccurrences.getFormItemSet().getOccurrences());
        }
    }
}