module api.form {

    export class FormItemView extends api.dom.DivEl {

        private context: api.form.FormContext;

        private formItem: FormItem;

        private parent: api.form.formitemset.FormItemSetOccurrenceView;

        private editContentRequestListeners: {(content: api.content.ContentSummary): void}[] = [];

        constructor(className: string, context: api.form.FormContext, formItem: FormItem,
                    parent: api.form.formitemset.FormItemSetOccurrenceView) {
            super(className);
            this.context = context;
            this.formItem = formItem;
            this.parent = parent;
        }

        getContext(): FormContext {
            return this.context;
        }

        getFormItem(): FormItem {
            return this.formItem;
        }

        getParent(): api.form.formitemset.FormItemSetOccurrenceView {
            return this.parent;
        }

        getParentDataPath(): api.data.DataPath {
            if (this.parent) {
                return this.parent.getDataPath();
            }
            return null;
        }

        getData(): api.data.Data[] {
            throw new Error("Method needs to be implemented in inheritor");
        }

        /*
         *  Override if inheriting FormItemView can provide attachments.
         */
        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(silent: boolean = true): api.form.ValidationRecording {

            // Default method to avoid having to implement method in Layout-s.
            return new api.form.ValidationRecording();
        }

        giveFocus(): boolean {
            return false;
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners.push(listener);
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners = this.editContentRequestListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyEditContentRequestListeners(content: api.content.ContentSummary) {
            this.editContentRequestListeners.forEach((listener) => {
                listener(content);
            })
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            //Should be implemented in child classes
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            //Should be implemented in child classes
        }
    }
}