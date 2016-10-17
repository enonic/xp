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

    export class FormOptionSetOccurrences extends FormSetOccurrences<FormOptionSetOccurrenceView> {

        private formOptionSet: FormOptionSet;

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
    }
}