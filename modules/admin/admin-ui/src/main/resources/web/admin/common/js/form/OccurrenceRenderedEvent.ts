module api.form {

    export class OccurrenceRenderedEvent {

        private occurrence: FormItemOccurrence<FormItemOccurrenceView>;

        private occurrenceView: FormItemOccurrenceView;

        private validateOnRender: boolean;

        constructor(occurrence: FormItemOccurrence<FormItemOccurrenceView>, occurrenceView: FormItemOccurrenceView,
                    validateViewOnRender: boolean = true) {
            this.occurrence = occurrence;
            this.occurrenceView = occurrenceView;
            this.validateOnRender = validateViewOnRender;
        }

        getOccurrence(): FormItemOccurrence<FormItemOccurrenceView> {
            return this.occurrence;
        }

        getOccurrenceView(): FormItemOccurrenceView {
            return this.occurrenceView;
        }

        validateViewOnRender(): boolean {
            return this.validateOnRender;
        }
    }
}
