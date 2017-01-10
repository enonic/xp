module api.form {

    import PropertySet = api.data.PropertySet;

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
    }
}