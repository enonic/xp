module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import PropertyPath = api.data.PropertyPath;

    export interface FormItemSetOccurrenceViewConfig {

        context: FormContext;

        formSetOccurrence: FormSetOccurrence<FormItemSetOccurrenceView>;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        dataSet: PropertySet;
    }

    export class FormItemSetOccurrenceView extends FormSetOccurrenceView {

        private formItemSet: FormItemSet;

        constructor(config: FormItemSetOccurrenceViewConfig) {
            super("form-item-set-occurrence-view", config.formSetOccurrence);
            this.occurrenceContainerClassName = "form-item-set-occurrences-container";
            this.formItemOccurrence = config.formSetOccurrence;
            this.formItemSet = config.formItemSet;
            this.propertySet = config.dataSet;

            this.formItemLayer = new FormItemLayer(config.context);
        }

        protected subscribeOnItemEvents() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onValidityChanged((event: RecordingValidityChangedEvent) => {

                    if (!this.currentValidationState) {
                        return; // currentValidationState is initialized on validate() call which may not be triggered in some cases
                    }

                    let previousValidState = this.currentValidationState.isValid();
                    if (event.isValid()) {
                        this.currentValidationState.removeByPath(event.getOrigin(), false, event.isIncludeChildren());
                    } else {
                        this.currentValidationState.flatten(event.getRecording());
                    }

                    if (previousValidState != this.currentValidationState.isValid()) {
                        this.notifyValidityChanged(new RecordingValidityChangedEvent(this.currentValidationState,
                            this.resolveValidationRecordingPath()).setIncludeChildren(true));
                    }
                });
            });
        }

        validate(silent: boolean = true): ValidationRecording {

            let allRecordings = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                let currRecording = formItemView.validate(silent);
                allRecordings.flatten(currRecording);
            });

            if (!silent) {
                if (allRecordings.validityChanged(this.currentValidationState)) {
                    this.notifyValidityChanged(new RecordingValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.currentValidationState = allRecordings;
            return allRecordings;
        }

        protected getFormSet(): FormSet {
            return this.formItemSet;
        }

        protected getFormItems(): FormItem[] {
            return this.formItemSet.getFormItems();
        }
    }

}
