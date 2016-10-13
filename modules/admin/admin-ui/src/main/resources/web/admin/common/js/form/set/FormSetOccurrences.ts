module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;

    export class FormSetOccurrences<V extends FormSetOccurrenceView> extends FormItemOccurrences<V> {

        protected context: FormContext;

        protected parent: FormSetOccurrenceView;

        protected occurrencesCollapsed: boolean;

        constructor(config: FormItemOccurrencesConfig) {
            super(config);
        }

        showOccurrences(show: boolean) {
            var views = this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formSetOccurrenceView: FormSetOccurrenceView) => {
                formSetOccurrenceView.showContainer(show);
            });
        }

        protected getSetFromArray(occurrence): PropertySet {
            var dataSet = this.propertyArray.getSet(occurrence.getIndex());
            if (!dataSet) {
                dataSet = this.propertyArray.addSet();
            }
            return dataSet;
        }

        isCollapsed(): boolean {
            return this.occurrencesCollapsed;
        }

        moveOccurrence(index: number, destinationIndex: number) {
            super.moveOccurrence(index, destinationIndex);
        }

        updateOccurrenceView(occurrenceView: FormSetOccurrenceView, propertyArray: PropertyArray,
                             unchangedOnly?: boolean): wemQ.Promise<void> {
            this.propertyArray = propertyArray;

            return occurrenceView.update(propertyArray);
        }
    }
}