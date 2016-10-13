module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;

    export interface FormOptionSetOccurrencesConfig {

        context: FormContext;

        occurrenceViewContainer: api.dom.Element;

        formOptionSet: FormOptionSet;

        parent: FormOptionSetOccurrenceView;

        propertyArray: PropertyArray;
    }

    export class FormOptionSetOccurrences extends FormItemOccurrences<FormOptionSetOccurrenceView> {

        private context: FormContext;

        private formOptionSet: FormOptionSet;

        private parent: FormOptionSetOccurrenceView;

        private occurrencesCollapsed: boolean;

        constructor(config: FormOptionSetOccurrencesConfig) {
            this.occurrencesCollapsed = false;
            this.context = config.context;
            this.formOptionSet = config.formOptionSet;
            this.parent = config.parent;

            super(<FormItemOccurrencesConfig>{
                formItem: config.formOptionSet,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formOptionSet.getOccurrences()
            });
        }

        getFormOptionSet(): FormOptionSet {
            return this.formOptionSet;
        }

        getAllowedOccurrences(): Occurrences {
            return this.formOptionSet.getOccurrences();
        }

        protected constructOccurrencesForNoData(): FormItemOccurrence<FormOptionSetOccurrenceView>[] {
            var occurrences: FormItemOccurrence<FormOptionSetOccurrenceView>[] = [];
            var minimumOccurrences = this.getAllowedOccurrences().getMinimum();

            if (minimumOccurrences > 0) {
                for (var i = 0; i < minimumOccurrences; i++) {
                    occurrences.push(this.createNewOccurrence(this, i));
                }
            } else if (this.context.getShowEmptyFormItemSetOccurrences()) { //TODO: rename check
                occurrences.push(this.createNewOccurrence(this, 0));
            }

            return occurrences;
        }

        protected constructOccurrencesForData(): FormItemOccurrence<FormOptionSetOccurrenceView>[] {
            var occurrences: FormItemOccurrence<FormOptionSetOccurrenceView>[] = [];

            this.propertyArray.forEach((property: Property, index: number) => {
                occurrences.push(this.createNewOccurrence(this, index));
            });

            if (occurrences.length < this.formOptionSet.getOccurrences().getMinimum()) {
                for (var index: number = occurrences.length; index < this.formOptionSet.getOccurrences().getMinimum(); index++) {
                    occurrences.push(this.createNewOccurrence(this, index));
                }
            }
            return occurrences;
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<FormOptionSetOccurrenceView>,
                            insertAtIndex: number): FormItemOccurrence<FormOptionSetOccurrenceView> {
            return new FormOptionSetOccurrence(<FormOptionSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence: FormOptionSetOccurrence): FormOptionSetOccurrenceView {

            var dataSet = this.getSetFromArray(occurrence);

            var newOccurrenceView = new FormOptionSetOccurrenceView(<FormOptionSetOccurrenceViewConfig>{
                context: this.context,
                formOptionSetOccurrence: occurrence,
                formOptionSet: this.formOptionSet,
                parent: this.parent,
                dataSet: dataSet
            });

            newOccurrenceView.onRemoveButtonClicked((event: RemoveButtonClickedEvent<FormOptionSetOccurrenceView>) => {
                this.removeOccurrenceView(event.getView());
            });
            return newOccurrenceView;
        }

        updateOccurrenceView(occurrenceView: FormOptionSetOccurrenceView, propertyArray: PropertyArray,
                             unchangedOnly?: boolean): wemQ.Promise<void> {
            this.propertyArray = propertyArray;

            return occurrenceView.update(propertyArray);
        }

        private getSetFromArray(occurrence): PropertySet {
            var dataSet = this.propertyArray.getSet(occurrence.getIndex());
            if (!dataSet) {
                dataSet = this.propertyArray.addSet();
            }
            return dataSet;
        }

        showOccurrences(show: boolean) {
            var views = <FormOptionSetOccurrenceView[]>this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formOptionSetOccurrenceView: FormOptionSetOccurrenceView) => {
                formOptionSetOccurrenceView.showContainer(show);
            });
        }

        isCollapsed(): boolean {
            return this.occurrencesCollapsed;
        }

        moveOccurrence(index: number, destinationIndex: number) {
            super.moveOccurrence(index, destinationIndex);
            //this.propertyArray.move(index, destinationIndex);
        }
    }
}