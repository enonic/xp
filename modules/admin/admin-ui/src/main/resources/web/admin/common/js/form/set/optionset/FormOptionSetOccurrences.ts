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

        constructor(config: FormOptionSetOccurrencesConfig) {
            super(<FormItemOccurrencesConfig>{
                formItem: config.formOptionSet,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.occurrenceViewContainer,
                allowedOccurrences: config.formOptionSet.getOccurrences()
            });

            this.context = config.context;
            this.formSet = config.formOptionSet;
            this.parent = config.parent;
            this.occurrencesCollapsed = false;
        }

        createNewOccurrenceView(occurrence: FormSetOccurrence<FormOptionSetOccurrenceView>): FormOptionSetOccurrenceView {

            let dataSet = this.getSetFromArray(occurrence);

            let newOccurrenceView = new FormOptionSetOccurrenceView(<FormOptionSetOccurrenceViewConfig>{
                context: this.context,
                formSetOccurrence: occurrence,
                formOptionSet: <FormOptionSet> this.formSet,
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