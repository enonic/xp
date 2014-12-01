module api.form {

    import Property = api.data2.Property;
    import PropertyArray = api.data2.PropertyArray;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;
    import PropertySet = api.data2.PropertySet;

    export interface InputViewConfig {

        context: FormContext;

        input: Input;

        parent: FormItemSetOccurrenceView;

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

        private validityChangedListeners: {(event: ValidityChangedEvent) : void}[] = [];

        constructor(config: InputViewConfig) {
            super(<FormItemViewConfig>{
                className: "input-view",
                context: config.context,
                formItem: config.input,
                parent: config.parent
            });

            api.util.assertNotNull(config.parentDataSet, "parentDataSet not expected to be null");
            api.util.assertNotNull(config.input, "input not expected to be null");

            this.input = config.input;
            this.parentPropertySet = config.parentDataSet;

            this.doLayout();
            this.refresh();
        }

        private doLayout() {

            if (this.input.getLabel()) {
                var label = new InputLabel(this.input);
                this.appendChild(label);
            } else {
                this.addClass("no-label")
            }

            var inputType: api.form.InputTypeName = this.input.getInputType();
            var inputTypeViewContext = this.getContext().createInputTypeViewContext(this.input.getInputTypeConfig(),
                this.parentPropertySet.getPropertyPath(), this.input);

            if (inputtype.InputTypeManager.isRegistered(inputType.getName())) {

                this.inputTypeView = inputtype.InputTypeManager.createView(inputType.getName(), inputTypeViewContext);
            }
            else {
                console.log("Input type [" + inputType.getName() + "] needs to be registered first.");
                this.inputTypeView = inputtype.InputTypeManager.createView("NoInputTypeFound", inputTypeViewContext);
            }

            this.inputTypeView.onEditContentRequest((content: api.content.ContentSummary) => {
                this.notifyEditContentRequested(content);
            });

            this.propertyArray = this.parentPropertySet.getPropertyArray(this.input.getName());
            if (!this.propertyArray) {
                this.propertyArray = PropertyArray.create().
                    setType(this.inputTypeView.getValueType()).
                    setName(this.input.getName()).
                    setParent(this.parentPropertySet).
                    build();
                this.parentPropertySet.addPropertyArray(this.propertyArray);
                var initialValue = this.inputTypeView.newInitialValue();
                if (initialValue) {
                    this.propertyArray.add(initialValue);
                }
            }

            this.inputTypeView.layout(this.input, this.propertyArray);

            this.appendChild(this.inputTypeView.getElement());

            if (!this.inputTypeView.isManagingAdd()) {

                var inputTypeViewNotManagingAdd = <api.form.inputtype.InputTypeViewNotManagingAdd<any>>this.inputTypeView;
                inputTypeViewNotManagingAdd.onOccurrenceAdded(() => {
                    this.refresh();
                });
                inputTypeViewNotManagingAdd.onOccurrenceRemoved((event: api.form.OccurrenceRemovedEvent) => {
                    this.refresh();

                    if (api.ObjectHelper.iFrameSafeInstanceOf(event.getOccurrenceView(), api.form.inputtype.support.InputOccurrenceView)) {
                        // force validate, since InputView might have become invalid
                        this.validate(false);
                    }
                });

                this.addButton = new api.ui.button.Button("Add");
                this.addButton.addClass("small");
                this.addButton.onClicked((event: MouseEvent) => {
                    inputTypeViewNotManagingAdd.createAndAddOccurrence();
                });

                this.bottomButtonRow = new api.dom.DivEl("bottom-button-row");
                this.appendChild(this.bottomButtonRow);
                this.bottomButtonRow.appendChild(this.addButton);
            }

            this.validationViewer = new api.form.ValidationRecordingViewer();
            this.appendChild(this.validationViewer);

            this.inputTypeView.onValidityChanged((event: api.form.inputtype.InputValidityChangedEvent)=> {

                this.handleInputValidationRecording(event.getRecording(), false);
            });
        }

        broadcastFormSizeChanged() {
            if (this.isVisible()) {
                this.inputTypeView.availableSizeChanged();
            }
        }

        refresh() {
            if (!this.inputTypeView.isManagingAdd()) {
                var inputTypeViewNotManagingAdd = <api.form.inputtype.InputTypeViewNotManagingAdd<any>>this.inputTypeView;
                this.addButton.setVisible(!inputTypeViewNotManagingAdd.maximumOccurrencesReached());
            }
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.inputTypeView.getAttachments();
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.propertyArray.getParentPropertyPath(), this.input.getName(),
                this.input.getOccurrences().getMinimum(),
                this.input.getOccurrences().getMaximum());
        }

        validate(silent: boolean = true): ValidationRecording {

            var inputRecording = this.inputTypeView.validate(silent);
            return this.handleInputValidationRecording(inputRecording, silent);
        }

        private handleInputValidationRecording(inputRecording: api.form.inputtype.InputValidationRecording,
                                               silent: boolean = true): ValidationRecording {

            var recording = new ValidationRecording();
            var validationRecordingPath = this.resolveValidationRecordingPath();

            if (inputRecording.isMinimumOccurrenesBreached()) {
                recording.breaksMinimumOccurrences(validationRecordingPath);
            }
            if (inputRecording.isMaximumOccurrenesBreached()) {
                recording.breaksMaximumOccurrences(validationRecordingPath);
            }

            if (!silent && recording.validityChanged(this.previousValidityRecording)) {
                this.notifyFormValidityChanged(new ValidityChangedEvent(recording, validationRecordingPath));
            }

            this.renderValidationErrors(recording);

            this.previousValidityRecording = recording;
            return recording;

        }

        giveFocus(): boolean {
            return this.inputTypeView.giveFocus();
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: ValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyFormValidityChanged(event: ValidityChangedEvent) {

            /*console.log("InputView[ " + event.getOrigin().toString() + " ] validity changed");
             if (event.getRecording().isValid()) {
             console.log(" valid!");
             }
             else {
             console.log(" invalid:");
             event.getRecording().print();
             }*/

            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        private renderValidationErrors(recording: ValidationRecording) {
            if (recording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }
            this.validationViewer.setObject(recording)
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
    }
}