module api.form.input {

    import Property = api.data.Property;
    import DataId = api.data.DataId;
    import DataSet = api.data.DataSet;
    import OccurrenceAddedEvent = api.form.OccurrenceAddedEvent;
    import OccurrenceRemovedEvent = api.form.OccurrenceRemovedEvent;
    import ValueAddedEvent = api.form.inputtype.ValueAddedEvent;
    import ValueRemovedEvent = api.form.inputtype.ValueRemovedEvent;
    import ValueChangedEvent = api.form.inputtype.ValueChangedEvent;
    import InputTypeManager = api.form.inputtype.InputTypeManager;

    export interface InputViewConfig {

        context: api.form.FormContext;

        input: api.form.Input;

        parent: api.form.formitemset.FormItemSetOccurrenceView;

        parentDataSet: DataSet;
    }

    export class InputView extends api.form.FormItemView {

        private parentDataSet: api.data.DataSet;

        private input: api.form.Input;

        private properties: Property[];

        private inputTypeView: api.form.inputtype.InputTypeView;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.Button;

        private previousValidityRecording: api.form.ValidationRecording;

        private validityChangedListeners: {(event: api.form.ValidityChangedEvent) : void}[] = [];

        private inputTypeViewSize: number = -1;

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

            if (InputTypeManager.isRegistered(inputType.getName())) {
                var inputTypeConfig = this.input.getInputTypeConfig();
                var inputTypeViewConfig = <api.form.inputtype.InputTypeViewConfig<any>> {
                    contentId: this.getContext().getContentId(),
                    contentPath: this.getContext().getContentPath(),
                    parentContentPath: this.getContext().getParentContentPath(),
                    inputConfig: inputTypeConfig,
                    attachments: this.getContext().getAttachments()
                };

                this.inputTypeView = InputTypeManager.createView(inputType.getName(), inputTypeViewConfig);
            }
            else {
                console.log("Input type [" + inputType.getName() + "] needs to be registered first.");
                this.inputTypeView = InputTypeManager.createView("NoInputTypeFound");
            }

            this.inputTypeView.addEditContentRequestListener((content: api.content.ContentSummary) => {
                this.notifyEditContentRequestListeners(content);
            });

            if (this.properties.length == 0) {
                var initialValue = this.inputTypeView.newInitialValue();
                if (initialValue != null) {
                    var initialProperty = new api.data.Property(this.input.getName(), new api.data.Value("", api.data.ValueTypes.STRING));
                    this.properties.push(initialProperty);
                    this.parentDataSet.addData(initialProperty);
                }
            }

            this.inputTypeView.layout(this.input, this.properties);

            this.inputTypeView.onValueAdded((event: ValueAddedEvent) => {

                var property = Property.fromNameValue(this.input.getName(), event.getValue());
                this.parentDataSet.addData(property);
            });

            this.inputTypeView.onValueRemoved((event: ValueRemovedEvent) => {

                var dataRemoved = this.parentDataSet.removeData(new DataId(this.input.getName(), event.getArrayIndex()));
            });

            this.inputTypeView.onValueChanged((event: ValueChangedEvent) => {

                var dataId = new DataId(this.input.getName(), event.getArrayIndex());
                var property: Property = this.parentDataSet.getPropertyFromDataId(dataId);
                property.setValue(event.getNewValue());
            });

            this.appendChild(this.inputTypeView.getElement());
            // TODO: Disabled for now since it causes performance slowdown when form as many InputView-s
            // TODO: onResized listening should be done on FormView instead and the broadcasted to any InputViews within the FormView
            //this.inputTypeView.getElement().onResized((event: api.dom.ElementResizedEvent) => {
            //    this.inputTypeView.availableSizeChanged(event.getNewWidth(), event.getNewHeight());
            //});


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

                this.addButton = new api.ui.Button("Add");
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

        private resolveValidationRecordingPath(): api.form.ValidationRecordingPath {

            return new api.form.ValidationRecordingPath(this.getParentDataPath(), this.input.getName());
        }

        validate(silent: boolean = true): api.form.ValidationRecording {

            var inputRecording = this.inputTypeView.validate(silent);
            return this.handleInputValidationRecording(inputRecording, silent);
        }

        private handleInputValidationRecording(inputRecording: api.form.inputtype.InputValidationRecording,
                                               silent: boolean = true): api.form.ValidationRecording {

            var recording = new api.form.ValidationRecording();
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

        onValidityChanged(listener: (event: api.form.ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.ValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: api.form.ValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyFormValidityChanged(event: api.form.ValidityChangedEvent) {

            /*console.log("InputView[ " + event.getOrigin().toString() + " ] validity changed");
             if (event.getRecording().isValid()) {
             console.log(" valid!");
             }
             else {
             console.log(" invalid:");
             event.getRecording().print();
             }*/

            this.validityChangedListeners.forEach((listener: (event: api.form.ValidityChangedEvent)=>void) => {
                listener(event);
            });
        }
    }
}