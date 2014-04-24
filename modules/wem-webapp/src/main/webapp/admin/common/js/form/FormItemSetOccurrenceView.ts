module api.form {

    import DataPath = api.data.DataPath;
    import DataPathElement = api.data.DataPathElement;
    import DataSet = api.data.DataSet;

    export interface FormItemSetOccurrenceViewConfig {

        context: FormContext;

        formItemSetOccurrence: FormItemSetOccurrence;

        formItemSet: FormItemSet;

        parent: FormItemSetOccurrenceView;

        dataSet: DataSet
    }

    export class FormItemSetOccurrenceView extends FormItemOccurrenceView {

        private context: FormContext;

        private formItemSetOccurrence: FormItemSetOccurrence;

        private formItemSet: FormItemSet;

        private removeButton: api.dom.AEl;

        private label: FormItemSetLabel;

        private constructedWithData: boolean;

        private parent: FormItemSetOccurrenceView;

        private dataSet: DataSet;

        private formItemViews: FormItemView[] = [];

        private formItemSetOccurrencesContainer: api.dom.DivEl;

        private validityChangedListeners: {(event: ValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: ValidationRecording;

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

        setDataSet(value: DataSet) {
            this.dataSet = value;
        }

        getDataPath(): DataPath {

            // TODO: Replace with just getting DataPath from this.dataSet?
            var parent: DataPath = this.parent != null ? this.parent.getDataPath() : null;
            if (parent == null) {
                return DataPath.fromPathElement(DataPathElement.fromDataId(this.formItemSetOccurrence.getDataId()));
            }
            else {
                return DataPath.fromParent(parent, DataPathElement.fromDataId(this.formItemSetOccurrence.getDataId()));
            }
        }

        getDataSet(): DataSet {
            return this.dataSet;
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


            this.formItemViews = new FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.formItemSet.getFormItems()).
                setParentElement(this.formItemSetOccurrencesContainer).
                setParent(this).
                layout(this.dataSet);

            this.validate(true);

            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onValidityChanged((event: ValidityChangedEvent) => {

                    var previousValidState = this.previousValidationRecording.isValid();
                    if (event.isValid()) {
                        this.previousValidationRecording.removeByPath(event.getOrigin());
                    }
                    else {
                        this.previousValidationRecording.flatten(event.getRecording());
                    }

                    if (previousValidState != this.previousValidationRecording.isValid()) {
                        this.notifyValidityChanged(new ValidityChangedEvent(this.previousValidationRecording,
                            this.resolveValidationRecordingPath()));
                    }
                });
            });
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

        public getInputView(name: string): InputView {

            var formItemView = this.getFormItemView(name);
            if (formItemView == null) {
                return null;
            }
            if (!(formItemView instanceof InputView)) {
                throw new Error("Found a FormItemView with name [" + name + "], but it was not an InputView");
            }
            return <InputView>formItemView;
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

        public getFormItemView(name: string): FormItemView {

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
                if (curr instanceof FieldSetView) {
                    var view = (<FieldSetView>curr).getFormItemView(name);
                    if (view != null) {
                        return view;
                    }
                }
            }

            return null;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments: api.content.attachment.Attachment[] = [];
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.getAttachments().forEach((attachment: api.content.attachment.Attachment) => {
                    attachments.push(attachment);
                });
            });
            return attachments;
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

        validate(silent: boolean = true): ValidationRecording {

            //console.log("FormItemSetOccurrenceView[ " + this.resolveValidationRecordingPath() + " ].validate(" + silent + ")");

            var allRecordings = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                var currRecording = formItemView.validate(silent);
                allRecordings.flatten(currRecording);

            });

            if (!silent) {
                if (allRecordings.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new ValidityChangedEvent(allRecordings, this.resolveValidationRecordingPath()));
                }
            }
            this.previousValidationRecording = allRecordings;
            return allRecordings;
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityChangedListeners.filter((currentListener: (event: ValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: ValidityChangedEvent) {

            /*console.log("FormItemSetOccurrenceView " + event.getOrigin().toString() + " validity changed: ");
             if (event.getRecording().isValid()) {
             console.log(" valid! ");
             }
             else {
             console.log(" invalid: ");
             event.getRecording().print();
             }*/

            this.validityChangedListeners.forEach((listener: (event: ValidityChangedEvent)=>void) => {
                listener(event);
            });
        }
    }

}