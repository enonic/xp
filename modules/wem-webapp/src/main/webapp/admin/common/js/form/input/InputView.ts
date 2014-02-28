module api.form.input {

    export class InputView extends api.form.FormItemView {

        private input: api.form.Input;

        private properties: api.data.Property[];

        private inputTypeView: api.form.inputtype.InputTypeView;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.Button;

        private previousValidityRecording: api.form.ValidationRecording;

        private validityChangedListeners: {(event: api.form.ValidityChangedEvent) : void}[] = [];

        constructor(context: api.form.FormContext, input: api.form.Input, parent: api.form.formitemset.FormItemSetOccurrenceView,
                    properties?: api.data.Property[]) {
            super("input-view", context, input, parent);
            this.input = input;
            this.properties = properties;

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

            this.inputTypeView.layout(this.input, this.properties);

            this.appendChild(this.inputTypeView.getElement());

            if (!this.inputTypeView.isManagingAdd()) {

                this.inputTypeView.onOccurrenceAdded(() => {
                    this.refresh();
                });
                this.inputTypeView.onOccurrenceRemoved((event: api.form.OccurrenceRemovedEvent) => {
                    this.refresh();

                    if (event.getOccurrenceView() instanceof api.form.inputtype.support.InputOccurrenceView) {
                        // force validate, since InputView might have become invalid
                        this.validate(false);
                    }
                });

                this.addButton = new api.ui.Button("Add");
                this.addButton.setClass("add-button");

                this.addButton.setClickListener(() => {
                    this.inputTypeView.createAndAddOccurrence();
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
                this.addButton.setVisible(!this.inputTypeView.maximumOccurrencesReached());
            }
        }

        getData(): api.data.Data[] {
            return this.getProperties();
        }

        getValue(index: number): api.data.Value {
            return this.inputTypeView.getValues()[index];
        }

        getProperties(): api.data.Property[] {
            var properties: api.data.Property[] = [];
            this.inputTypeView.getValues().forEach((value: api.data.Value, index: number) => {
                properties[index] = new api.data.Property(this.input.getName(), value);
            });
            return properties;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.inputTypeView.getAttachments();
        }

        private resolveValidationRecordingPath(): api.form.ValidationRecordingPath {

            return new api.form.ValidationRecordingPath(this.getParentDataPath(), this.input.getName());
        }

        validate(silent: boolean = true): api.form.ValidationRecording {

            //console.log("InputView[ " + this.resolveValidationRecordingPath() + " ].validate(" + silent + ")");

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