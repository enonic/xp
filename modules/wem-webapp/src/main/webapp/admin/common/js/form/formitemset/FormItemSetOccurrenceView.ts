module api.form.formitemset {

    import DataPath = api.data.DataPath;
    import DataPathElement = api.data.DataPathElement;
    import DataSet = api.data.DataSet;
    import support = api.form.inputtype.support;

    export interface FormItemSetOccurrenceViewConfig {

        context: api.form.FormContext;

        formItemSetOccurrence: FormItemSetOccurrence;

        formItemSet: api.form.FormItemSet;

        parent: FormItemSetOccurrenceView;

        dataSet: DataSet
    }

    export class FormItemSetOccurrenceView extends api.form.FormItemOccurrenceView {

        private context: api.form.FormContext;

        private formItemSetOccurrence: FormItemSetOccurrence;

        private formItemSet: api.form.FormItemSet;

        private removeButton: api.dom.AEl;

        private label: FormItemSetLabel;

        private constructedWithData: boolean;

        private parent: FormItemSetOccurrenceView;

        private dataSet: DataSet;

        private formItemViews: api.form.FormItemView[] = [];

        private formItemSetOccurrencesContainer: api.dom.DivEl;

        private validityChangedListeners: {(event: api.form.ValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.ValidationRecording;

        constructor(config: FormItemSetOccurrenceViewConfig) {
            super("form-item-set-occurrence-view", config.formItemSetOccurrence);
            this.context = config.context;
            this.formItemSetOccurrence = config.formItemSetOccurrence;
            this.formItemSet = config.formItemSet;
            this.parent = config.parent;
            this.constructedWithData = config.dataSet != null;
            this.dataSet = config.dataSet;
            this.doLayout();
            this.refresh();
        }

        getDataPath(): DataPath {

            var parent: DataPath = this.parent != null ? this.parent.getDataPath() : null;
            if (parent == null) {
                return DataPath.fromPathElement(DataPathElement.fromDataId(this.formItemSetOccurrence.getDataId()));
            }
            else {
                return DataPath.fromParent(parent, DataPathElement.fromDataId(this.formItemSetOccurrence.getDataId()));
            }
        }

        private doLayout() {
            this.removeButton = new api.dom.AEl("remove-button");
            this.appendChild(this.removeButton);
            this.removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveButtonClicked();
            });

            this.label = new FormItemSetLabel(this.formItemSet);
            this.appendChild(this.label);

            this.formItemSetOccurrencesContainer = new api.dom.DivEl("form-item-set-occurrences-container");
            this.appendChild(this.formItemSetOccurrencesContainer);


            this.formItemViews = new api.form.FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                setParent(this).
                layout(this.dataSet);

            this.validate(true);

            this.formItemViews.forEach((formItemView: api.form.FormItemView) => {
                formItemView.onValidityChanged((event: api.form.ValidityChangedEvent) => {

                    var previousValidState = this.previousValidationRecording.isValid();
                    if (event.isValid()) {
                        this.previousValidationRecording.removeByPath(event.getOrigin());
                    }
                    else {
                        this.previousValidationRecording.flatten(event.getRecording());
                    }

                    if (previousValidState != this.previousValidationRecording.isValid()) {
                        this.notifyValidityChanged(new api.form.ValidityChangedEvent(this.previousValidationRecording,
                            this.resolveValidationRecordingPath()));
                    }
                });
            });
        }

        getFormItemViews(): api.form.FormItemView[] {
            return this.formItemViews;
        }

        refresh() {

            if (!this.formItemSetOccurrence.oneAndOnly()) {
                this.label.addClass("drag-control");
            } else {
                this.label.removeClass("drag-control");
            }

            this.getEl().setData("dataId", this.formItemSetOccurrence.getDataId().toString());

            this.removeButton.setVisible(this.formItemSetOccurrence.showRemoveButton());
        }

        public getValueAtPath(path: DataPath): api.data.Value {
            if (path == null) {
                throw new Error("To get a value, a path is required");
            }
            if (path.elementCount() == 0) {
                throw new Error("Cannot get value from empty path: " + path.toString());
            }

            if (path.elementCount() == 1) {
                return this.getValue(path.getFirstElement().toDataId());
            }
            else {
                return this.forwardGetValueAtPath(path);
            }
        }

        public getValue(dataId: api.data.DataId): api.data.Value {

            var inputView = this.getInputView(dataId.getName());
            if (inputView == null) {
                return null;
            }
            return inputView.getValue(dataId.getArrayIndex());
        }

        private forwardGetValueAtPath(path: DataPath): api.data.Value {

            var dataId: api.data.DataId = path.getFirstElement().toDataId();
            var formItemSetView = this.getFormItemSetView(dataId.getName());
            if (formItemSetView == null) {
                return null;
            }
            var formItemSetOccurrenceView = formItemSetView.getFormItemSetOccurrenceView(dataId.getArrayIndex());
            return formItemSetOccurrenceView.getValueAtPath(path.newWithoutFirstElement());
        }

        public getInputView(name: string): api.form.input.InputView {

            var formItemView = this.getFormItemView(name);
            if (formItemView == null) {
                return null;
            }
            if (!(formItemView instanceof api.form.input.InputView)) {
                throw new Error("Found a FormItemView with name [" + name + "], but it was not an InputView");
            }
            return <api.form.input.InputView>formItemView;
        }

        public getFormItemSetView(name: string): FormItemSetView {

            var formItemView = this.getFormItemView(name);
            if (formItemView == null) {
                return null;
            }
            if (!(formItemView instanceof FormItemSetView)) {
                throw new Error("Found a FormItemView with name [" + name + "], but it was not an FormItemSetView");
            }
            return <FormItemSetView>formItemView;
        }

        public getFormItemView(name: string): api.form.FormItemView {

            // TODO: Performance could be improved if the views where accessible by name from a map

            for (var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if (name == curr.getFormItem().getName()) {
                    return curr;
                }
            }

            // FormItemView not found - look inside FieldSet-s
            for (var i = 0; i < this.formItemViews.length; i++) {
                var curr = this.formItemViews[i];
                if (curr instanceof api.form.layout.FieldSetView) {
                    var view = (<api.form.layout.FieldSetView>curr).getFormItemView(name);
                    if (view != null) {
                        return view;
                    }
                }
            }

            return null;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments: api.content.attachment.Attachment[] = [];
            this.formItemViews.forEach((formItemView: api.form.FormItemView) => {
                formItemView.getAttachments().forEach((attachment: api.content.attachment.Attachment) => {
                    attachments.push(attachment);
                });
            });
            return attachments;
        }

        toggleContainer(show: boolean) {
            if (show) {
                this.formItemSetOccurrencesContainer.show();
            } else {
                this.formItemSetOccurrencesContainer.hide();
            }
        }

        private resolveValidationRecordingPath(): api.form.ValidationRecordingPath {
            return new api.form.ValidationRecordingPath(this.getDataPath(), null);
        }

        getLastValidationRecording(): api.form.ValidationRecording {
            return this.previousValidationRecording;
        }

        validate(silent: boolean = true): api.form.ValidationRecording {

            //console.log("FormItemSetOccurrenceView[ " + this.resolveValidationRecordingPath() + " ].validate(" + silent + ")");

            var allRecordings = new api.form.ValidationRecording();
            this.formItemViews.forEach((formItemView: api.form.FormItemView) => {
                var currRecording = formItemView.validate(silent);
                allRecordings.flatten(currRecording);

            });

            if (!silent) {
                if (allRecordings.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.ValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.previousValidationRecording = allRecordings;
            return allRecordings;
        }

        onValidityChanged(listener: (event: api.form.ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.ValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: api.form.ValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: api.form.ValidityChangedEvent) {

            /*console.log("FormItemSetOccurrenceView " + event.getOrigin().toString() + " validity changed: ");
            if (event.getRecording().isValid()) {
                console.log(" valid! ");
            }
            else {
                console.log(" invalid: ");
                event.getRecording().print();
            }*/

            this.validityChangedListeners.forEach((listener: (event: api.form.ValidityChangedEvent)=>void) => {
                listener(event);
            });
        }
    }

}