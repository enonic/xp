module api.form {

    import FormEventNames = api.form.event.FormEventNames;
    import FormEvent = api.form.event.FormEvent;
    import FormValidityChangedEvent = api.form.event.FormValidityChangedEvent;

    export class FormView extends api.ui.Panel {

        private context: FormContext;

        private form: Form;

        private rootDataSet: api.data.RootDataSet;

        private formItemViews: FormItemView[] = [];

        private listeners: {[eventName:string]:{(event:api.form.event.FormEvent):void}[]} = {};

        private isValid:boolean;

        constructor(context: FormContext, form: Form, contentData?: api.data.RootDataSet) {
            super("form-view");
            this.context = context;
            this.form = form;
            this.rootDataSet = contentData;
            this.isValid = true;

            this.listeners[FormEventNames.FormValidityChanged] = [];
            this.doLayout();
        }

        private doLayout() {
            this.formItemViews = new FormItemLayer().
                setFormContext(this.context).
                setFormItems(this.form.getFormItems()).
                setParentElement(this).
                layout(this.rootDataSet);
            this.formItemViews.forEach((formItemView:FormItemView) => {
                formItemView.onValidityChanged((event:api.form.inputtype.support.ValidityChangedEvent) => {
                    this.formItemValidityChangedListener(event);
                });
            });
        }

        private formItemValidityChangedListener(event:api.form.inputtype.support.ValidityChangedEvent) {
            var isValid:boolean = this.formItemViews.every((formItemView:FormItemView) => {
                var recorder:ValidationRecorder = new ValidationRecorder();
                formItemView.validate(recorder);
                return recorder.valid();
            });

            if (isValid != this.isValid) {
                this.isValid = isValid;
                this.notifyValidityChanged(new FormValidityChangedEvent(isValid));
            }
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

        public getFormItemSetView(name: string): api.form.formitemset.FormItemSetView {

            var formItemView = this.getFormItemView(name);
            if (formItemView == null) {
                return null;
            }
            if (!(formItemView instanceof api.form.formitemset.FormItemSetView)) {
                throw new Error("Found a FormItemView with name [" + name + "], but it was not an FormItemSetView");
            }
            return <api.form.formitemset.FormItemSetView>formItemView;
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
                if (curr instanceof api.form.layout.FieldSetView) {
                    var view = (<api.form.layout.FieldSetView>curr).getFormItemView(name);
                    if (view != null) {
                        return view;
                    }
                }
            }

            return null;
        }

        getOriginalData(): api.data.RootDataSet {
            return this.rootDataSet;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            var attachments: api.content.attachment.Attachment[] = [];
            this.formItemViews.forEach((formItemView: FormItemView) => {
                attachments = attachments.concat(formItemView.getAttachments());
            });
            return attachments;
        }

        getData(): api.content.ContentData {
            var contentData: api.content.ContentData = new api.content.ContentData();
            this.formItemViews.forEach((formItemView: FormItemView) => {

                formItemView.getData().forEach((data: api.data.Data) => {
                    contentData.addData(data)
                });

            });
            return contentData;
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

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView:FormItemView) => {
                formItemView.addEditContentRequestListener(listener);
            });
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.formItemViews.forEach((formItemView:FormItemView) => {
                formItemView.removeEditContentRequestListener(listener);
            });
        }

        private notifyEditContentRequestListeners(content: api.content.ContentSummary) {
            this.formItemViews.forEach((formItemView:FormItemView) => {
                formItemView.notifyEditContentRequestListeners(content);
            })
        }

        private addListener(eventName:FormEventNames, listener:(event:FormEvent)=>void) {
            this.listeners[eventName].push(listener);
        }

        onValidityChanged(listener:(event:FormValidityChangedEvent)=>void) {
            this.addListener(FormEventNames.FormValidityChanged, listener);
        }

        private removeListener(eventName:FormEventNames, listener:(event:FormEvent)=>void) {
            this.listeners[eventName].filter((currentListener:(event:FormEvent)=>void) => {
                return listener == currentListener;
            });
        }

        unValidityChanged(listener:(event:FormValidityChangedEvent)=>void) {
            this.removeListener(FormEventNames.FormValidityChanged, listener);
        }

        private notifyListeners(eventName:FormEventNames, event:FormEvent) {
            this.listeners[eventName].forEach((listener:(event:FormEvent)=>void) => {
                listener(event);
            });
        }

        notifyValidityChanged(event:FormValidityChangedEvent) {
            this.notifyListeners(FormEventNames.FormValidityChanged, event);
        }

    }
}