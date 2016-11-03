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

        private formOptionSet: FormOptionSet;

        constructor(config: FormOptionSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-option-set-view",
                context: config.context,
                formItem: config.formOptionSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formOptionSet = config.formOptionSet;
            this.classPrefix = "form-option-set";
            this.helpText = this.formOptionSet.getHelpText();

            this.addClass(this.formOptionSet.getPath().getElements().length % 2 ? "even" : "odd");
        }

        protected initOccurrences(): FormSetOccurrences<FormOptionSetOccurrenceView> {
            return this.formItemOccurrences = new FormOptionSetOccurrences(<FormOptionSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formOptionSet: this.formOptionSet,
                parent: this.getParent(),
                propertyArray: this.getPropertyArray(this.parentDataSet)
            });
        }

        protected getPropertyArray(parentPropertySet: PropertySet): PropertyArray {
            var existingPropertyArray = parentPropertySet.getPropertyArray(this.formOptionSet.getName());
            if (!existingPropertyArray) {
                parentPropertySet.addPropertySet(this.formOptionSet.getName());
            }
            return parentPropertySet.getPropertyArray(this.formOptionSet.getName());
        }

        protected getLabel(): string {
            return this.formOptionSet.getLabel();
        }

        protected getOccurrences(): api.form.Occurrences {
            return this.formOptionSet.getOccurrences();
        }

        protected resolveValidationRecordingPath(): ValidationRecordingPath {
            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formOptionSet.getName(),
                this.formOptionSet.getOccurrences().getMinimum(), this.formOptionSet.getOccurrences().getMaximum());
        }
    }
}