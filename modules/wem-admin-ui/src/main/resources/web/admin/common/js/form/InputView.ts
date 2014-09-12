module api.form {

    import Property = api.data.Property;
    import DataId = api.data.DataId;
    import DataSet = api.data.DataSet;

    export interface InputViewConfig {

        context: FormContext;

        input: Input;

        parent: FormItemSetOccurrenceView;

        parentDataSet: DataSet;
    }

    export class InputView extends api.form.FormItemView {

        private parentDataSet: api.data.DataSet;

        private input: Input;

        private properties: Property[];

        private inputTypeView: api.form.inputtype.InputTypeView;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.button.Button;

        private previousValidityRecording: ValidationRecording;

        private validityChangedListeners: {(event: ValidityChangedEvent) : void}[] = [];

        constructor(config: InputViewConfig) {
            super(<FormItemViewConfig>{
                className: "input-view",
                context: config.context,
                formItem: config.input,
                parent: config.parent
            });

            api.util.assertNotNull(config.parentDataSet, "parentDataSet not exected to be null");
            api.util.assertNotNull(config.input, "input not exected to be null");

            this.input = config.input;
            this.parentDataSet = config.parentDataSet;
            this.properties = config.parentDataSet.getPropertiesByName(config.input.getName());

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
                this.getParentDataPath(),
                this.input);
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

            if (this.properties.length == 0) {

                var initialRawValue: any = this.inputTypeView.newInitialValue();
                if (api.ObjectHelper.iFrameSafeInstanceOf(initialRawValue, api.data.Value)) {
                    throw new Error(api.util.getClassName(this.inputTypeView) +
                                    ".newInitialValue must not return a api.data.Value, but the raw value instead");
                }
                var initialValue = new api.data.Value(initialRawValue, this.inputTypeView.getValueType());
                var initialProperty = new api.data.Property(this.input.getName(), initialValue);
                this.properties.push(initialProperty);
                this.parentDataSet.addData(initialProperty);
            }

            this.inputTypeView.layout(this.input, this.properties);

            this.inputTypeView.onValueAdded((event: inputtype.ValueAddedEvent) => {

                var property = Property.fromNameValue(this.input.getName(), event.getValue());
                this.parentDataSet.addData(property);
            });

            this.inputTypeView.onValueRemoved((event: inputtype.ValueRemovedEvent) => {

                var dataRemoved = this.parentDataSet.removeData(new DataId(this.input.getName(), event.getArrayIndex()));
            });

            this.inputTypeView.onValueChanged((event: inputtype.ValueChangedEvent) => {

                api.util.assertNotNull(event.getNewValue(), "sending ValueChangedEvent-s for null values is not allowed");

                var dataId = new DataId(this.input.getName(), event.getArrayIndex());
                var property: Property = this.parentDataSet.getPropertyById(dataId);
                if (property != null) {
                    property.setValue(event.getNewValue());
                }
                else {
                    var property = Property.fromNameValue(this.input.getName(), event.getNewValue());
                    this.parentDataSet.addData(property);
                }
            });

            this.appendChild(this.inputTypeView.getElement());

            if (!this.inputTypeView.isManagingAdd()) {

                var inputTypeViewNotManagingAdd: api.form.inputtype.InputTypeViewNotManagingAdd = <api.form.inputtype.InputTypeViewNotManagingAdd>this.inputTypeView;
                inputTypeViewNotManagingAdd.onOccurrenceAdded(() => {
                    this.refresh();
                });
                inputTypeViewNotManagingAdd.onOccurrenceRemoved((event: api.form.OccurrenceRemovedEvent) => {
                    this.refresh();

                    if (event.getOccurrenceView() instanceof api.form.inputtype.support.InputOccurrenceView) {
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
                var inputTypeViewNotManagingAdd: api.form.inputtype.InputTypeViewNotManagingAdd = <api.form.inputtype.InputTypeViewNotManagingAdd>this.inputTypeView;
                this.addButton.setVisible(!inputTypeViewNotManagingAdd.maximumOccurrencesReached());
            }
        }

        getValue(index: number): api.data.Value {
            return this.inputTypeView.getValues()[index];
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.inputTypeView.getAttachments();
        }

        private resolveValidationRecordingPath(): ValidationRecordingPath {

            return new ValidationRecordingPath(this.getParentDataPath(), this.input.getName());
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

            if (!silent) {
                if (recording.validityChanged(this.previousValidityRecording)) {
                    this.notifyFormValidityChanged(new ValidityChangedEvent(recording, validationRecordingPath));
                }
            }

            if (recording.isValid()) {
                this.removeClass("invalid");
                this.addClass("valid");
            }
            else {
                this.removeClass("valid");
                this.addClass("invalid");
            }

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