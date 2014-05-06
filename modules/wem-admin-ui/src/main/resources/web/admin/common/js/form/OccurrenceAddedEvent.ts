module api.form {

    export class OccurrenceAddedEvent {

        private occurrence: FormItemOccurrence<FormItemOccurrenceView>;

        private occurrenceView: FormItemOccurrenceView;

        constructor(occurrence: FormItemOccurrence<FormItemOccurrenceView>, occurrenceView: FormItemOccurrenceView) {
            this.occurrence = occurrence;
            this.occurrenceView = occurrenceView;
        }

        getOccurrence(): FormItemOccurrence<FormItemOccurrenceView> {
            return this.occurrence;
        }

        getOccurrenceView(): FormItemOccurrenceView {
            return this.occurrenceView;
        }
    }
}