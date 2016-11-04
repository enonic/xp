module api.form {

    export class FormSetOccurrence<V extends FormSetOccurrenceView> extends FormItemOccurrence<V> {

        constructor(formSetOccurrences: FormSetOccurrences<V>, index: number) {
            super(formSetOccurrences, index, formSetOccurrences.getFormSet().getOccurrences());
        }
    }
}