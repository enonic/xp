module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;

    export class FormSetOccurrences<V extends FormSetOccurrenceView> extends FormItemOccurrences<V> {

        protected context: FormContext;

        protected parent: FormSetOccurrenceView;

        protected occurrencesCollapsed: boolean = false;

        protected formSet: FormSet;

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

        getFormSet(): FormSet {
            return this.formSet;
        }

        getAllowedOccurrences(): Occurrences {
            return this.formSet.getOccurrences();
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<V>,
                            insertAtIndex: number): FormItemOccurrence<V> {
            return new FormSetOccurrence(<FormSetOccurrences<V>>formItemOccurrences, insertAtIndex);
        }

        protected getSetFromArray(occurrence): PropertySet {
            var dataSet = this.propertyArray.getSet(occurrence.getIndex());
            if (!dataSet) {
                dataSet = this.propertyArray.addSet();
            }
            return dataSet;
        }

        protected constructOccurrencesForNoData(): FormItemOccurrence<V>[] {
            var occurrences: FormItemOccurrence<V>[] = [];
            var minimumOccurrences = this.getAllowedOccurrences().getMinimum();

            if (minimumOccurrences > 0) {
                for (var i = 0; i < minimumOccurrences; i++) {
                    occurrences.push(this.createNewOccurrence(this, i));
                }
            } else if (this.context.getShowEmptyFormItemSetOccurrences()) {
                occurrences.push(this.createNewOccurrence(this, 0));
            }

            return occurrences;
        }

        protected constructOccurrencesForData(): FormItemOccurrence<V>[] {
            var occurrences: FormItemOccurrence<V>[] = [];

            this.propertyArray.forEach((property: Property, index: number) => {
                occurrences.push(this.createNewOccurrence(this, index));
            });

            if (occurrences.length < this.getAllowedOccurrences().getMinimum()) {
                for (var index: number = occurrences.length; index < this.getAllowedOccurrences().getMinimum(); index++) {
                    occurrences.push(this.createNewOccurrence(this, index));
                }
            }
            return occurrences;
        }

        toggleHelpText(show?: boolean) {
            this.getOccurrenceViews().forEach((view) => {
                view.toggleHelpText(show);
            })
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

        resetOccurrenceView(occurrenceView: FormSetOccurrenceView) {
            occurrenceView.reset();
        }
    }
}