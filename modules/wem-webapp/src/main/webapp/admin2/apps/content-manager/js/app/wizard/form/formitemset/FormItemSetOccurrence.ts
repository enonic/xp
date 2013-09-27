module app_wizard_form_formitemset {

    /*
     * Represents an occurrence of many. Translates to a DataSet in the data domain.
     */
    export class FormItemSetOccurrence extends app_wizard_form.FormItemOccurrence {

        constructor(formItemSetOccurrences:FormItemSetOccurrences, index:number) {
            super(formItemSetOccurrences, index, formItemSetOccurrences.getFormItemSet().getOccurrences());
        }
    }
}