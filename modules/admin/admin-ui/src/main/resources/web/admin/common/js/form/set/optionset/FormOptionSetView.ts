module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormOptionSetViewConfig {

        context: FormContext;

        formOptionSet: FormOptionSet;

        parent: FormOptionSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormOptionSetView extends FormSetView<FormOptionSetOccurrenceView> {

        constructor(config: FormOptionSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-option-set-view",
                context: config.context,
                formItem: config.formOptionSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formSet = config.formOptionSet;
            this.classPrefix = "form-option-set";
            this.helpText = this.formSet.getHelpText();

            this.addClass(this.formSet.getPath().getElements().length % 2 ? "even" : "odd");
            if (this.formSet.getOccurrences().getMaximum() == 1) {
                this.addClass("max-1-occurrence");
            }
        }

        protected initOccurrences(): FormSetOccurrences<FormOptionSetOccurrenceView> {
            return this.formItemOccurrences = new FormOptionSetOccurrences(<FormOptionSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formOptionSet: <FormOptionSet> this.formSet,
                parent: this.getParent(),
                propertyArray: this.getPropertyArray(this.parentDataSet)
            });
        }

        protected getPropertyArray(parentPropertySet: PropertySet): PropertyArray {
            var propertyArray = parentPropertySet.getPropertyArray(this.formSet.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().setType(ValueTypes.DATA).setName(this.formSet.getName()).setParent(
                    this.parentDataSet).build();
                parentPropertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }
    }
}