module api.form {

    import PropertySet = api.data.PropertySet;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export interface FormItemSetViewConfig {

        context: FormContext;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class FormItemSetView extends FormSetView<FormItemSetOccurrenceView> {

        constructor(config: FormItemSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-item-set-view",
                context: config.context,
                formItem: config.formItemSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formSet = config.formItemSet;
            this.classPrefix = "form-item-set";
            this.helpText = this.formSet.getHelpText();

            this.addClass(this.formSet.getPath().getElements().length % 2 ? "even" : "odd");
            if (this.formSet.getOccurrences().getMaximum() == 1) {
                this.addClass("max-1-occurrence");
            }

        }

        protected initOccurrences(): FormSetOccurrences<FormItemSetOccurrenceView> {
            return this.formItemOccurrences = new FormItemSetOccurrences(<FormItemSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formItemSet: <FormItemSet> this.formSet,
                parent: this.getParent(),
                propertyArray: this.getPropertyArray(this.parentDataSet)
            });
        }

        protected getPropertyArray(propertySet: PropertySet): PropertyArray {
            var propertyArray = propertySet.getPropertyArray(this.formSet.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().setType(ValueTypes.DATA).setName(this.formSet.getName()).setParent(
                    this.parentDataSet).build();
                propertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }
    }
}