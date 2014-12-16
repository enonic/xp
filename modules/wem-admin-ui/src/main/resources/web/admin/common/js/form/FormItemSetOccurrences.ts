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
    export class FormItemSetOccurrences extends FormItemOccurrences<FormItemSetOccurrenceView> {

        private context: FormContext;

        private formItemSet: FormItemSet;

        private parent: FormItemSetOccurrenceView;

        private occurrencesCollapsed: boolean;

        constructor(config: FormItemSetOccurrencesConfig) {
            this.occurrencesCollapsed = false;

            super(<FormItemOccurrencesConfig>{
                formItem: config.formItemSet,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formItemSet.getOccurrences()
            });
            this.context = config.context;
            this.formItemSet = config.formItemSet;
            this.parent = config.parent;

            var dataSetCount = this.propertyArray.getSize();
            if (dataSetCount > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        getFormItemSet(): FormItemSet {
            return this.formItemSet;
        }

        getAllowedOccurrences(): Occurrences {
            return this.formItemSet.getOccurrences();
        }

        constructOccurrencesForNoData() {

            var minimumOccurrences = this.getAllowedOccurrences().getMinimum();
            if (minimumOccurrences > 0) {

                for (var i = 0; i < minimumOccurrences; i++) {
                    this.addOccurrence(this.createNewOccurrence(this, i));
                }
            }
            else {
                if (this.context.getShowEmptyFormItemSetOccurrences()) {
                    this.addOccurrence(this.createNewOccurrence(this, 0));
                }
            }
        }

        private constructOccurrencesForData() {
            this.propertyArray.forEach((property: Property, index: number) => {
                this.addOccurrence(new FormItemSetOccurrence(this, index));
            });

            if (this.countOccurrences() < this.formItemSet.getOccurrences().getMinimum()) {
                for (var index: number = this.countOccurrences(); index < this.formItemSet.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(new FormItemSetOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<FormItemSetOccurrenceView>,
                            insertAtIndex: number): FormItemOccurrence<FormItemSetOccurrenceView> {
            return new FormItemSetOccurrence(<FormItemSetOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence: FormItemSetOccurrence): FormItemSetOccurrenceView {

            var dataSet = this.propertyArray.getSet(occurrence.getIndex());
            if (!dataSet) {
                dataSet = this.propertyArray.addSet();
            }
            var newOccurrenceView = new FormItemSetOccurrenceView(<FormItemSetOccurrenceViewConfig>{
                context: this.context,
                formItemSetOccurrence: occurrence,
                formItemSet: this.formItemSet,
                parent: this.parent,
                dataSet: dataSet
            });

            newOccurrenceView.onRemoveButtonClicked((event: RemoveButtonClickedEvent<FormItemSetOccurrenceView>) => {
                this.doRemoveOccurrence(event.getView(), event.getIndex());
            });
            return newOccurrenceView;
        }

        showOccurences(show: boolean) {
            var views = <FormItemSetOccurrenceView[]>this.getOccurrenceViews();
            this.occurrencesCollapsed = !show;
            views.forEach((formItemSetOccurenceView: FormItemSetOccurrenceView) => {
                formItemSetOccurenceView.showContainer(show);
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