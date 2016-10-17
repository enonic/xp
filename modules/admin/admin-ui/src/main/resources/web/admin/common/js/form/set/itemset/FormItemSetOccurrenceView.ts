module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import PropertyPath = api.data.PropertyPath;

    export interface FormItemSetOccurrenceViewConfig {

        context: FormContext;

        formItemSetOccurrence: FormItemSetOccurrence;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        dataSet: PropertySet
    }

    export class FormItemSetOccurrenceView extends FormSetOccurrenceView {

        private formItemSet: FormItemSet;

        constructor(config: FormItemSetOccurrenceViewConfig) {
            super("form-item-set-occurrence-view", config.formItemSetOccurrence);
            this.formItemOccurrence = config.formItemSetOccurrence;
            this.formItemSet = config.formItemSet;
            this.propertySet = config.dataSet;

            this.formItemLayer = new FormItemLayer(config.context);
        }

        getDataPath(): PropertyPath {

            return this.propertySet.getProperty().getPath();
        }

        public layout(validate: boolean = true): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();

            this.removeButton = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveButtonClicked();
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.label = new FormOccurrenceDraggableLabel(this.formItemSet.getLabel(), this.formItemSet.getOccurrences());
            this.appendChild(this.label);

            if (this.formItemSet.getHelpText()) {
                this.helpText = new HelpTextContainer(this.formItemSet.getHelpText());

                this.label.appendChild(this.helpText.getToggler());
                this.appendChild(this.helpText.getHelpText());

                this.toggleHelpText(this.formItemSet.isHelpTextOn());
            }

            this.formItemSetOccurrencesContainer = new api.dom.DivEl("form-item-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);


            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                setParent(this).
                layout(this.propertySet, validate);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                this.formItemViews = formItemViews;
                if (validate) {
                    this.validate(true);
                }

                this.formItemViews.forEach((formItemView: FormItemView) => {
                    formItemView.onValidityChanged((event: RecordingValidityChangedEvent) => {

                        if (!this.currentValidationState) {
                            return; // currentValidationState is initialized on validate() call which may not be triggered in some cases
                        }

                        var previousValidState = this.currentValidationState.isValid();
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

                this.refresh();
                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        validate(silent: boolean = true): ValidationRecording {

            var allRecordings = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                var currRecording = formItemView.validate(silent);
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
    }

}