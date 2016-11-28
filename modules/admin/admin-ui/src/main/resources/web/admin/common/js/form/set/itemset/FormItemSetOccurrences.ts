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

        constructor(config: FormItemSetOccurrencesConfig) {
            super(<FormItemOccurrencesConfig>{
                formItem: config.formItemSet,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formItemSet.getOccurrences()
            });

            this.context = config.context;
            this.formSet = config.formItemSet;
            this.parent = config.parent;
            this.occurrencesCollapsed = false;
        }

        createNewOccurrenceView(occurrence: FormSetOccurrence<FormItemSetOccurrenceView>): FormItemSetOccurrenceView {

            var dataSet = this.getSetFromArray(occurrence);

            var newOccurrenceView = new FormItemSetOccurrenceView(<FormItemSetOccurrenceViewConfig>{
                context: this.context,
                formSetOccurrence: occurrence,
                formItemSet: <FormItemSet> this.formSet,
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