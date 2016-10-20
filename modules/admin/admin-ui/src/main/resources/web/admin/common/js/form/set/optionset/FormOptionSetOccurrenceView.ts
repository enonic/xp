module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyArray = api.data.PropertyArray;
    import PropertyPath = api.data.PropertyPath;
    import ValueTypes = api.data.ValueTypes;
    import Value = api.data.Value;

    export interface FormOptionSetOccurrenceViewConfig {
        context: FormContext;

        formOptionSetOccurrence: FormOptionSetOccurrence;

        formOptionSet: FormOptionSet;

        parent: FormItemOccurrenceView;

        dataSet: PropertySet
    }

    export class FormOptionSetOccurrenceView extends FormSetOccurrenceView {

        private formOptionSet: FormOptionSet;

        private context: FormContext;

        constructor(config: FormOptionSetOccurrenceViewConfig) {
            super("form-option-set-occurrence-view", config.formOptionSetOccurrence);
            this.formItemOccurrence = config.formOptionSetOccurrence;
            this.formOptionSet = config.formOptionSet;
            this.propertySet = config.dataSet;
            this.ensureSelectionArrayExists(this.propertySet);

            this.formItemLayer = new FormItemLayer(config.context);
            this.context = config.context;
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

            this.label = new FormOccurrenceDraggableLabel(this.formOptionSet.getLabel(), this.formOptionSet.getOccurrences());
            this.appendChild(this.label);

            this.formItemSetOccurrencesContainer = new api.dom.DivEl("form-option-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);

            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(this.formOptionSet.getFormItems()).
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

                        if (this.isNew()) {
                            this.currentValidationState = new ValidationRecording();
                            return;
                        }

                        var previousValidState = this.currentValidationState.isValid();
                        if (previousValidState != event.isValid()) { //one form item may affect validity state of whole option set
                            this.validate(); // so let's re-validate it all
                        } else { // otherwise, just update validation state
                            if (event.isValid()) {
                                this.currentValidationState.removeByPath(event.getOrigin(), false, event.isIncludeChildren());
                            } else {
                                this.currentValidationState.flatten(event.getRecording());
                            }
                        }

                        if (previousValidState != this.currentValidationState.isValid()) {
                            this.notifyValidityChanged(new RecordingValidityChangedEvent(this.currentValidationState,
                                this.resolveValidationRecordingPath()).setIncludeChildren(true));
                        }
                    });

                    (<FormOptionSetOptionView> formItemView).onSelectionChanged((viewToSkipValidation: FormOptionSetOptionView) => {
                        if (!this.currentValidationState) {
                            return; // currentValidationState is initialized on validate() call which may not be triggered in some cases
                        }

                        if (this.isNew()) {
                            this.currentValidationState = new ValidationRecording();
                            return;
                        }

                        var previousValidState = this.currentValidationState.isValid();
                        this.validate(true, viewToSkipValidation);
                        if (this.currentValidationState.isValid()) {
                            this.currentValidationState.removeByPath(this.resolveValidationRecordingPath(), true);
                        } else {
                            this.currentValidationState.flatten(this.currentValidationState);
                        }

                        if (previousValidState != this.currentValidationState.isValid()) {
                            this.notifyValidityChanged(new RecordingValidityChangedEvent(this.currentValidationState,
                                this.resolveValidationRecordingPath()).setIncludeChildren(true));
                        }
                    })
                });

                this.refresh();
                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        private isNew(): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(this.context, api.content.form.ContentFormContext)) {
                var contentFormContext = <api.content.form.ContentFormContext> this.context;
                if (contentFormContext.getFormState() != null && contentFormContext.getFormState().isNew()) {
                    return true;
                }
            }
            return false;
        }

        private makeMultiselectionNote(): string {
            var multiselection = this.formOptionSet.getMultiselection();
            if (multiselection.getMinimum() == 1 && multiselection.getMaximum() == 1) {
                return null;
            }

            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() == 0) {
                return "(any)"
            }
            if (multiselection.getMinimum() > 0 && multiselection.getMaximum() == 0) {
                return "(at least " + multiselection.getMinimum() + ")";
            }
            if (multiselection.getMinimum() > 1 && multiselection.getMinimum() == multiselection.getMaximum()) {
                return "(pick " + multiselection.getMinimum() + ")";
            }
            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() > 1) {
                return "(up to " + multiselection.getMaximum() + ")";
            }
            if (multiselection.getMinimum() > 0 && multiselection.getMaximum() > multiselection.getMinimum()) {
                return "(" + multiselection.getMinimum() + " to " + multiselection.getMaximum() + ")";
            }
            if (multiselection.getMinimum() == 0 && multiselection.getMaximum() == 1) {
                return "(0 or 1)";
            }
            return null;
        }

        private ensureSelectionArrayExists(propertyArraySet: PropertySet) {
            var selectionPropertyArray = propertyArraySet.getPropertyArray(this.formOptionSet.getName() + "_selection");
            if (!selectionPropertyArray) {
                selectionPropertyArray = PropertyArray.create().
                    setType(ValueTypes.STRING).
                    setName(this.formOptionSet.getName() + "_selection").
                    setParent(propertyArraySet).
                    build();
                propertyArraySet.addPropertyArray(selectionPropertyArray);
                this.addDefaultSelectionToSelectionArray(selectionPropertyArray);
            }
        }

        private addDefaultSelectionToSelectionArray(selectionPropertyArray: PropertyArray) {
            this.formOptionSet.getOptions().forEach((option: FormOptionSetOption) => {
                if (option.isDefaultOption() && selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMaximum()) {
                    selectionPropertyArray.add(new Value(option.getName(), new api.data.ValueTypeString()))
                }
            });
        }

        validate(silent: boolean = true, viewToSkipValidation?: FormOptionSetOptionView): ValidationRecording {

            var allRecordings = new ValidationRecording();

            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (viewToSkipValidation != formItemView) {
                    var currRecording = formItemView.validate(silent);
                    allRecordings.flatten(currRecording);
                }
            });

            allRecordings.flatten(this.validateMultiselection());

            if (!silent) {
                if (allRecordings.validityChanged(this.currentValidationState)) {
                    this.notifyValidityChanged(new RecordingValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.currentValidationState = allRecordings;
            return allRecordings;
        }

        private validateMultiselection(): ValidationRecording {
            var multiselectionRecording = new ValidationRecording(),
                validationRecordingPath = this.resolveValidationRecordingPath(),
                selectionPropertyArray = this.propertySet.getPropertyArray(this.formOptionSet.getName() + "_selection");

            if (selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMinimum()) {
                multiselectionRecording.breaksMinimumOccurrences(validationRecordingPath);
            }

            if (this.formOptionSet.getMultiselection().maximumBreached(selectionPropertyArray.getSize())) {
                multiselectionRecording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (this.currentValidationState) {
                if (selectionPropertyArray.getSize() < this.formOptionSet.getMultiselection().getMinimum()) {
                    this.currentValidationState.breaksMinimumOccurrences(validationRecordingPath);
                } else {
                    this.currentValidationState.removeUnreachedMinimumOccurrencesByPath(validationRecordingPath, false);
                }

                if (this.formOptionSet.getMultiselection().maximumBreached(selectionPropertyArray.getSize())) {
                    this.currentValidationState.breaksMaximumOccurrences(validationRecordingPath);
                } else {
                    this.currentValidationState.removeBreachedMaximumOccurrencesByPath(validationRecordingPath, false);
                }
            }

            return multiselectionRecording;
        }


    }
}