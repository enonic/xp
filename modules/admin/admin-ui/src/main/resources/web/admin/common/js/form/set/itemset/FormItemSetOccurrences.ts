module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormItemSetOccurrencesConfig {

        context: FormContext;

        occurrenceViewContainer: api.dom.Element;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        propertyArray: PropertyArray;
    }

    /*
     * A kind of a controller, which adds/removes FormItemSetOccurrenceView-s
     */
    export class FormItemSetOccurrences extends FormSetOccurrences<FormItemSetOccurrenceView> {

        private formItemSet: FormItemSet;

        constructor(config: FormItemSetOccurrencesConfig) {
            this.occurrencesCollapsed = false;
            this.context = config.context;
            this.formItemSet = config.formItemSet;
            this.parent = config.parent;

            super(<FormItemOccurrencesConfig>{
                formItem: config.formItemSet,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formItemSet.getOccurrences()
            });
        }

        getFormItemSet(): FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences(): Occurrences {
            return this.formItemSet.getOccurrences();
        }

        protected constructOccurrencesForNoData(): FormItemOccurrence<FormItemSetOccurrenceView>[] {
            var occurrences: FormItemOccurrence<FormItemSetOccurrenceView>[] = [];
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

        protected constructOccurrencesForData(): FormItemOccurrence<FormItemSetOccurrenceView>[] {
            var occurrences: FormItemOccurrence<FormItemSetOccurrenceView>[] = [];

            this.propertyArray.forEach((property: Property, index: number) => {
                occurrences.push(this.createNewOccurrence(this, index));
            });

            if (occurrences.length < this.formItemSet.getOccurrences().getMinimum()) {
                for (var index: number = occurrences.length; index < this.formItemSet.getOccurrences().getMinimum(); index++) {
                    occurrences.push(this.createNewOccurrence(this, index));
                }
            }
            return occurrences;
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<FormItemSetOccurrenceView>,
                            insertAtIndex: number): FormItemOccurrence<FormItemSetOccurrenceView> {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence: FormItemSetOccurrence): FormItemSetOccurrenceView {

            var dataSet = this.getSetFromArray(occurrence);

            var newOccurrenceView = new FormItemSetOccurrenceView(<FormItemSetOccurrenceViewConfig>{
                context: this.context,
                formItemSetOccurrence: occurrence,
                formItemSet: this.formItemSet,
                parent: this.parent,
                dataSet: dataSet
            });

            newOccurrenceView.onRemoveButtonClicked((event: RemoveButtonClickedEvent<FormItemSetOccurrenceView>) => {
                this.removeOccurrenceView(event.getView());
            });
            return newOccurrenceView;
        }
    }
}