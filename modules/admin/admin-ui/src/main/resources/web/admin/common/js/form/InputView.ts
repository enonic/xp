module api.form {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import PropertySet = api.data.PropertySet;
    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import i18n = api.util.i18n;

    export interface InputViewConfig {

        context: FormContext;

        input: Input;

        parent: FormItemOccurrenceView;

        parentDataSet: PropertySet;
    }

    export class InputView extends api.form.FormItemView {

        private input: Input;

        private parentPropertySet: PropertySet;

        private propertyArray: PropertyArray;

        private inputTypeView: api.form.inputtype.InputTypeView<any>;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.button.Button;

        private validationViewer: api.form.ValidationRecordingViewer;

        private previousValidityRecording: ValidationRecording;

        private userInputValid: boolean;

        private validityChangedListeners: {(event: RecordingValidityChangedEvent): void}[] = [];

        private helpText: HelpTextContainer;

        public static debug: boolean = false;

        constructor(config: InputViewConfig) {
            super(<FormItemViewConfig>{
                className: 'input-view',
                context: config.context,
                formItem: config.input,
                parent: config.parent
            });

            api.util.assertNotNull(config.parentDataSet, 'parentDataSet not expected to be null');
            api.util.assertNotNull(config.input, 'input not expected to be null');

            this.input = config.input;
            this.parentPropertySet = config.parentDataSet;

        }

        public layout(validate: boolean = true): wemQ.Promise<void> {

            if (this.input.getInputType().getName().toLowerCase() !== 'checkbox') { //checkbox input type generates clickable label itself
                if (this.input.getLabel()) {
                    let label = new InputLabel(this.input);
                    this.appendChild(label);
                } else {
                    this.addClass('no-label');
                }
            }

            if (this.input.getHelpText()) {
                this.helpText = new HelpTextContainer(this.input.getHelpText());

                this.appendChild(this.helpText.getToggler());
            }

            if (this.input.isMaximizeUIInputWidth()) {
                this.addClass('label-over-input');
            }

            this.inputTypeView = this.createInputTypeView();
            this.inputTypeView.onEditContentRequest((content: api.content.ContentSummary) => {
                this.notifyEditContentRequested(content);
            });

            this.propertyArray = this.getPropertyArray(this.parentPropertySet);

            return this.inputTypeView.layout(this.input, this.propertyArray).then(() => {
                this.appendChild(this.inputTypeView.getElement());

                if (!!this.helpText) {
                    this.appendChild(this.helpText.getHelpText());
                }

                if (!this.inputTypeView.isManagingAdd()) {

                    let inputTypeViewNotManagingAdd = <BaseInputTypeNotManagingAdd<any>>this.inputTypeView;
                    inputTypeViewNotManagingAdd.onOccurrenceAdded(() => {
                        this.refresh();
                    });
                    inputTypeViewNotManagingAdd.onOccurrenceRemoved((event: api.form.OccurrenceRemovedEvent) => {
                        this.refresh();

                        if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(),
                                api.form.inputtype.support.InputOccurrenceView)) {
                            // force validate, since InputView might have become invalid
                            this.validate(false);
                        }
                    });

                    this.addButton = new api.ui.button.Button(i18n('action.add'));
                    this.addButton.addClass('small');
                    this.addButton.onClicked((event: MouseEvent) => {
                        inputTypeViewNotManagingAdd.createAndAddOccurrence();
                    });

                    this.bottomButtonRow = new api.dom.DivEl('bottom-button-row');
                    this.appendChild(this.bottomButtonRow);
                    this.bottomButtonRow.appendChild(this.addButton);
                }

                this.validationViewer = new ValidationRecordingViewer();
                this.appendChild(this.validationViewer);

                this.inputTypeView.onValidityChanged((event: api.form.inputtype.InputValidityChangedEvent)=> {
                    this.handleInputValidationRecording(event.getRecording(), false);
                });

                this.refresh(validate);
            });
        }

        private getPropertyArray(propertySet: PropertySet): PropertyArray {
            let array = propertySet.getPropertyArray(this.input.getName());
            if (!array) {
                array = PropertyArray.create().setType(this.inputTypeView.getValueType()).setName(this.input.getName()).setParent(
                    this.parentPropertySet).build();

                propertySet.addPropertyArray(array);

                let initialValue = this.input.getDefaultValue();
                if (!initialValue) {
                    initialValue = this.inputTypeView.newInitialValue();
                }
                if (initialValue) {
                    array.add(initialValue);
                }
            }
            return array;
        }

        public update(propertySet: PropertySet, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (InputView.debug) {
                console.debug('InputView.update' + (unchangedOnly ? ' ( unchanged only)' : ''), this, propertySet);
            }
            // update parent first because it can be used in getPropertyArray
            this.parentPropertySet = propertySet;
            this.propertyArray = this.getPropertyArray(propertySet);

            return this.inputTypeView.update(this.propertyArray, unchangedOnly);
        }

        public reset() {
            this.inputTypeView.reset();
        }

        public getInputTypeView(): api.form.inputtype.InputTypeView<any> {
            return this.inputTypeView;
        }

        private createInputTypeView(): api.form.inputtype.InputTypeView<any> {
            let inputType: api.form.InputTypeName = this.input.getInputType();
            let inputTypeViewContext = this.getContext().createInputTypeViewContext(
                this.input.getInputTypeConfig() || {},
                this.parentPropertySet.getPropertyPath(),
                this.input
            );

            if (inputtype.InputTypeManager.isRegistered(inputType.getName())) {
                return inputtype.InputTypeManager.createView(inputType.getName(), inputTypeViewContext);
            } else {
                console.warn('Input type [' + inputType.getName() + '] needs to be registered first.');
                return inputtype.InputTypeManager.createView('NoInputTypeFound', inputTypeViewContext);
            }
        }

        broadcastFormSizeChanged() {
            if (this.isVisible()) {
                this.inputTypeView.availableSizeChanged();
            }
        }

        refresh(validate: boolean = true) {
            if (!this.inputTypeView.isManagingAdd()) {
                let inputTypeViewNotManagingAdd = <BaseInputTypeNotManagingAdd<any>>this.inputTypeView;
                this.addButton.setVisible(!inputTypeViewNotManagingAdd.maximumOccurrencesReached());
            }
            if (validate) {
                this.validate(false);
            }
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.propertyArray.getParentPropertyPath(), this.input.getName(),
                this.input.getOccurrences().getMinimum(),
                this.input.getOccurrences().getMaximum());
        }

        public displayValidationErrors(value: boolean) {
            this.inputTypeView.displayValidationErrors(value);
        }

        hasValidUserInput(): boolean {

            return this.inputTypeView.hasValidUserInput();
        }

        validate(silent: boolean = true): ValidationRecording {

            let inputRecording = this.inputTypeView.validate(silent);
            return this.handleInputValidationRecording(inputRecording, silent);
        }

        private handleInputValidationRecording(inputRecording: api.form.inputtype.InputValidationRecording,
                                               silent: boolean = true): ValidationRecording {

            let recording = new ValidationRecording();
            let validationRecordingPath = this.resolveValidationRecordingPath();
            let hasValidInput = this.hasValidUserInput();

            if (inputRecording.isMinimumOccurrencesBreached()) {
                recording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (inputRecording.isMaximumOccurrencesBreached()) {
                recording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (recording.validityChanged(this.previousValidityRecording) || this.userInputValidityChanged(hasValidInput)) {
                if (!silent) {
                    this.notifyValidityChanged(new RecordingValidityChangedEvent(recording,
                        validationRecordingPath).setInputValueBroken(!hasValidInput));
                }
                this.toggleClass('highlight-validity-change', this.highlightOnValidityChange());
            }

            if (!silent && (recording.validityChanged(this.previousValidityRecording) || this.userInputValidityChanged(hasValidInput) )) {
                this.notifyValidityChanged(new RecordingValidityChangedEvent(recording,
                    validationRecordingPath).setInputValueBroken(!hasValidInput));
            }

            this.previousValidityRecording = recording;
            this.userInputValid = hasValidInput;

            this.renderValidationErrors(recording, inputRecording.getAdditionalValidationRecord());
            return recording;
        }

        userInputValidityChanged(currentState: boolean): boolean {
            return this.userInputValid == null || this.userInputValid == null || !(this.userInputValid === currentState);
        }

        giveFocus(): boolean {
            return this.inputTypeView.giveFocus();
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: RecordingValidityChangedEvent)=>void) => {
                return listener === currentListener;
            });
        }

        private notifyValidityChanged(event: RecordingValidityChangedEvent) {

            this.validityChangedListeners.forEach((listener: (event: RecordingValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        private renderValidationErrors(recording: ValidationRecording, additionalValidationRecord: AdditionalValidationRecord) {
            if (!this.mayRenderValidationError()) {
                return;
            }

            if (recording.isValid() && this.hasValidUserInput()) {
                this.removeClass('invalid');
                this.addClass('valid');
            } else {
                this.removeClass('valid');
                this.addClass('invalid');
            }

            this.validationViewer.setObject(recording);

            if (additionalValidationRecord && additionalValidationRecord.isOverwriteDefault()) {
                this.validationViewer.appendValidationMessage(additionalValidationRecord.getMessage());
            }
        }

        private mayRenderValidationError(): boolean {
            return this.input.getInputType().getName() !== 'SiteConfigurator';
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.inputTypeView.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.inputTypeView.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.inputTypeView.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.inputTypeView.unBlur(listener);
        }

        toggleHelpText(show?: boolean) {
            if (!!this.helpText) {
                this.helpText.toggleHelpText(show);
            }
        }

        hasHelpText(): boolean {
            return !!this.input.getHelpText();
        }
    }
}
