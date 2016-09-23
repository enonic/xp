module api.form.optionset {

    export class FormOptionSetOccurrence extends FormItemOccurrence<FormOptionSetOccurrenceView> {

        constructor(formOptionSetOccurrences: FormOptionSetOccurrences, index: number) {
            super(formOptionSetOccurrences, index, formOptionSetOccurrences.getFormOptionSet().getOccurrences());
        }
    }
}