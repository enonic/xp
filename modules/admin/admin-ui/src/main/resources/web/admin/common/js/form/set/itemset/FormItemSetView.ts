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

        private formItemSet: FormItemSet;

        constructor(config: FormItemSetViewConfig) {
            super(<FormItemViewConfig> {
                className: "form-item-set-view",
                context: config.context,
                formItem: config.formItemSet,
                parent: config.parent
            });
            this.parentDataSet = config.parentDataSet;
            this.formItemSet = config.formItemSet;
            this.classPrefix = "form-item-set";
            this.helpText = this.formItemSet.getHelpText();

            this.addClass(this.formItemSet.getPath().getElements().length % 2 ? "even" : "odd");
        }

        protected getLabel(): string {
            return this.formItemSet.getLabel();
        }

        protected initOccurrences(): FormSetOccurrences<FormItemSetOccurrenceView> {
            return this.formItemOccurrences = new FormItemSetOccurrences(<FormItemSetOccurrencesConfig>{
                context: this.getContext(),
                occurrenceViewContainer: this.occurrenceViewsContainer,
                formItemSet: this.formItemSet,
                parent: this.getParent(),
                propertyArray: this.getPropertyArray(this.parentDataSet)
            });
        }

        protected getPropertyArray(propertySet: PropertySet): PropertyArray {
            var propertyArray = propertySet.getPropertyArray(this.formItemSet.getName());
            if (!propertyArray) {
                propertyArray = PropertyArray.create().setType(ValueTypes.DATA).setName(this.formItemSet.getName()).setParent(
                    this.parentDataSet).build();
                propertySet.addPropertyArray(propertyArray);
            }
            return propertyArray;
        }

        protected getOccurrences(): api.form.Occurrences {
            return this.formItemSet.getOccurrences();
        }

        protected resolveValidationRecordingPath(): ValidationRecordingPath {
            return new ValidationRecordingPath(this.parentDataSet.getPropertyPath(), this.formItemSet.getName(),
                this.formItemSet.getOccurrences().getMinimum(), this.formItemSet.getOccurrences().getMaximum());
        }
    }
}