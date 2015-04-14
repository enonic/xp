module api.form {

    import PropertySet = api.data.PropertySet;
    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import PropertyTree = api.data.PropertyTree;

    export interface FormItemSetOccurrenceViewConfig {

        context: FormContext;

        formItemSetOccurrence: FormItemSetOccurrence;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        dataSet: PropertySet
    }

    export class FormItemSetOccurrenceView extends FormItemOccurrenceView {

        private context: FormContext;

        private formItemSetOccurrence: FormItemSetOccurrence;

        private formItemSet: FormItemSet;

        private removeButton: api.dom.AEl;

        private label: FormItemSetLabel;

        private constructedWithData: boolean;

        private parent: FormItemSetOccurrenceView;

        private propertySet: PropertySet;

        private formItemViews: FormItemView[] = [];

        private formItemSetOccurrencesContainer: api.dom.DivEl;

        private validityChangedListeners: {(event: RecordingValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: ValidationRecording;

        constructor(config: FormItemSetOccurrenceViewConfig) {
            super("form-item-set-occurrence-view", config.formItemSetOccurrence);
            this.context = config.context;
            this.formItemSetOccurrence = config.formItemSetOccurrence;
            this.formItemSet = config.formItemSet;
            this.parent = config.parent;
            this.constructedWithData = config.dataSet != null;
            this.propertySet = config.dataSet;
        }

        getDataPath(): PropertyPath {

            return this.propertySet.getProperty().getPath();
        }

        public layout(): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();

            this.removeButton = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveButtonClicked();
            });

            this.label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(this.label);

            this.formItemSetOccurrencesContainer = new api.dom.DivEl("form-item-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);


            var layoutPromise: wemQ.Promise<FormItemView[]> = new FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                setParent(this).
                layout(this.propertySet);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                this.formItemViews = formItemViews;
                this.validate(true);

                this.formItemViews.forEach((formItemView: FormItemView) => {
                    formItemView.onValidityChanged((event: RecordingValidityChangedEvent) => {

                        var previousValidState = this.previousValidationRecording.isValid();
                        if (event.isValid()) {
                            this.previousValidationRecording.removeByPath(event.getOrigin());
                        }
                        else {
                            this.previousValidationRecording.flatten(event.getRecording());
                        }

                        if (previousValidState != this.previousValidationRecording.isValid()) {
                            this.notifyValidityChanged(new RecordingValidityChangedEvent(this.previousValidationRecording,
                                this.resolveValidationRecordingPath()));
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

        getFormItemViews(): FormItemView[] {
            return this.formItemViews;
        }

        giveFocus() {
            var focusGiven = false;
            this.getFormItemViews().forEach((formItemView: FormItemView) => {
                if (!focusGiven && formItemView.giveFocus()) {
                    focusGiven = true;
                }
            });
            return focusGiven;
        }

        refresh() {

            if (!this.formItemSetOccurrence.oneAndOnly()) {
                this.label.addClass("drag-control");
            } else {
                this.label.removeClass("drag-control");
            }

            this.removeButton.setVisible(this.formItemSetOccurrence.isRemoveButtonRequired());
        }

        showContainer(show: boolean) {
            if (show) {
                this.formItemSetOccurrencesContainer.show();
            } else {
                this.formItemSetOccurrencesContainer.hide();
            }
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {
            return new ValidationRecordingPath(this.getDataPath(), null);
        }

        getLastValidationRecording(): ValidationRecording {
            return this.previousValidationRecording;
        }

        public displayValidationErrors(value: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.displayValidationErrors(value);
            });
        }

        hasValidUserInput(): boolean {

            var result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });
            return result;
        }


        validate(silent: boolean = true): ValidationRecording {

            var allRecordings = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                var currRecording = formItemView.validate(silent);
                allRecordings.flatten(currRecording);

            });

            if (!silent) {
                if (allRecordings.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new RecordingValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.previousValidationRecording = allRecordings;
            return allRecordings;
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: RecordingValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: RecordingValidityChangedEvent) {

            /*console.log("FormItemSetOccurrenceView " + event.getOrigin().toString() + " validity changed: ");
             if (event.getRecording().isValid()) {
             console.log(" valid! ");
             }
             else {
             console.log(" invalid: ");
             event.getRecording().print();
             }*/

            this.validityChangedListeners.forEach((listener: (event: RecordingValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onFocus(listener);
            });
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unFocus(listener);
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onBlur(listener);
            });
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unBlur(listener);
            });
        }
    }

}