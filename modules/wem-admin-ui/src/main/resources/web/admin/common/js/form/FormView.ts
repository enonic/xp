module api.form {

    export class FormView extends api.ui.panel.Panel {

        private context: FormContext;

        private form: Form;

        private rootDataSet: api.data.RootDataSet;

        private formItemViews: FormItemView[] = [];

        private formValidityChangedListeners: {(event: FormValidityChangedEvent):void}[] = [];

        private previousValidationRecording: ValidationRecording;

        private width: number;

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor(context: FormContext, form: Form, rootDataSet?: api.data.RootDataSet) {
            super("form-view");
            this.context = context;
            this.form = form;
            this.rootDataSet = rootDataSet;

            this.doLayout();
        }

        private doLayout() {

            this.formItemViews = new FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.form.getFormItems()).
                setParentElement(this).
                layout(this.rootDataSet);

            this.formItemViews.forEach((formItemView: FormItemView) => {

                formItemView.onFocus((event: FocusEvent) => {
                    this.notifyFocused(event);
                });

                formItemView.onBlur((event: FocusEvent) => {
                    this.notifyBlurred(event);
                });

                formItemView.onValidityChanged((event: ValidityChangedEvent) => {

                    if (!this.previousValidationRecording) {
                        this.previousValidationRecording = event.getRecording();
                        this.notifyValidityChanged(new FormValidityChangedEvent(this.previousValidationRecording));
                    }
                    else {
                        var previousValidState = this.previousValidationRecording.isValid();

                        if (event.isValid()) {
                            this.previousValidationRecording.removeByPath(event.getOrigin());
                        }
                        else {
                            this.previousValidationRecording.flatten(event.getRecording());
                        }

                        if (previousValidState != this.previousValidationRecording.isValid()) {
                            this.notifyValidityChanged(new FormValidityChangedEvent(this.previousValidationRecording));
                        }
                    }
                });
            });

            api.dom.Window.get().onResized((event: UIEvent) => this.checkSizeChanges(), this);
            this.onShown(() => this.checkSizeChanges());
        }

        private checkSizeChanges() {
            if (this.isVisible() && this.isSizeChanged()) {
                this.preserveCurrentSize();
                this.broadcastFormSizeChanged();
            }
        }

        private preserveCurrentSize() {
            this.width = this.getEl().getWidth();
        }

        private isSizeChanged(): boolean {
            return this.width != this.getEl().getWidth();
        }

        private broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        public validate(): ValidationRecording {

            var allRecordings: ValidationRecording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                var currRecording = formItemView.validate(true);
                allRecordings.flatten(currRecording);
            });

            this.previousValidationRecording = allRecordings;

            //console.log("FormView.validate:");
            //allRecordings.print();
            return allRecordings;
        }

        public isValid(): boolean {

            return this.validate().isValid();
        }

        public getValueAtPath(path: api.data.DataPath): api.data.Value {
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

        private forwardGetValueAtPath(path: api.data.DataPath): api.data.Value {

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

        getData(): api.data.RootDataSet {
            return this.rootDataSet;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments: api.content.attachment.Attachment[] = [];
            this.formItemViews.forEach((formItemView: FormItemView) => {
                attachments = attachments.concat(formItemView.getAttachments());
            });
            return attachments;
        }

        giveFocus(): boolean {
            var focusGiven = false;
            if (this.formItemViews.length > 0) {
                for (var i = 0; i < this.formItemViews.length; i++) {
                    if (this.formItemViews[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onEditContentRequest(listener);
            });
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.unEditContentRequest(listener);
            });
        }

        onValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.formValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.formValidityChangedListeners =
            this.formValidityChangedListeners.filter((currentListener: (event: FormValidityChangedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifyValidityChanged(event: FormValidityChangedEvent) {
            //console.log("FormView.validityChanged");
            //if (event.getRecording().isValid()) {
            //    console.log(" valid: ");
            //}
            //else {
            //    console.log(" invalid: ");
            //    event.getRecording().print();
            //}

            this.formValidityChangedListeners.forEach((listener: (event: FormValidityChangedEvent)=>void)=> {
                listener.call(this, event);
            })
        }

        private notifyEditContentRequested(content: api.content.ContentSummary) {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.notifyEditContentRequested(content);
            })
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}