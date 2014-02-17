module api.form.input {

    export class InputView extends api.form.FormItemView {

        private input: api.form.Input;

        private properties: api.data.Property[];

        private inputTypeView: api.form.inputtype.InputTypeView;

        private bottomButtonRow: api.dom.DivEl;

        private addButton: api.ui.Button;

        constructor(context: api.form.FormContext, input: api.form.Input, properties?: api.data.Property[]) {
            super("input-view", context, input);
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
                    dataPath: api.data.DataPath.fromString(this.input.getPath().toString()),
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
            if (this.inputTypeView instanceof api.form.inputtype.support.BaseInputTypeView) {
                this.appendChild(<api.form.inputtype.support.BaseInputTypeView>this.inputTypeView);
            }
            else {
                this.appendChild(api.dom.Element.fromHtmlElement(this.inputTypeView.getHTMLElement()))
            }

            if (!this.inputTypeView.isManagingAdd()) {

                this.inputTypeView.addFormItemOccurrencesListener(<api.form.FormItemOccurrencesListener>{
                    onOccurrenceAdded: (occurrenceAdded: api.form.FormItemOccurrence<any>) => {
                        this.refresh();
                    },
                    onOccurrenceRemoved: (occurrenceRemoved: api.form.FormItemOccurrence<any>) => {
                        this.refresh();
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

            this.inputTypeView.onValidityChanged((event: inputtype.support.ValidityChangedEvent)=> {

                console.log("InputView[" + this.input.getPath().toString() + "].inputTypeView.onValidityChanged -> " + event.isValid());

                if (event.isValid()) {
                    this.removeClass("invalid");
                }
                else {
                    this.addClass("invalid");
                }
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

        validate(silent: boolean = true): api.form.ValidationRecording {

            var recording = this.inputTypeView.validate(silent);

            if (recording.isValid()) {
                this.removeClass("invalid");
            }
            else {
                this.addClass("invalid");
            }

            console.log("InputView[" + this.input.getPath().toString() + "].validate: " + recording.isValid());
            recording.print();

            return recording;
        }

        hasValidOccurrences(): boolean {

            return this.getData().length >= this.input.getOccurrences().getMaximum();
        }

        giveFocus(): boolean {
            return this.inputTypeView.giveFocus();
        }

        onValidityChanged(listener: (event: api.form.inputtype.support.ValidityChangedEvent)=>void) {
            this.inputTypeView.onValidityChanged(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.support.ValidityChangedEvent)=>void) {
            this.inputTypeView.unValidityChanged(listener);
        }
    }
}