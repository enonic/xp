module api.form {

    export class FormItemView extends api.dom.DivEl {

        private context:api.form.FormContext;

        private formItem:FormItem;

        constructor(idPrefix:string, className:string, context:api.form.FormContext, formItem:FormItem) {
            super(idPrefix, className);
            this.context = context;
            this.formItem = formItem;
        }

        getContext():FormContext {
            return this.context;
        }

        getFormItem():FormItem {
            return this.formItem;
        }

        getData():api.data.Data[] {
            throw new Error("Method needs to be implemented in inheritor");
        }

        /*
         *  Override if inheriting FormItemView can provide attachments.
         */
        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(validationRecorder:ValidationRecorder) {

            // Default method to avoid having to implement method in Layout-s.
        }

        hasValidOccurrences():boolean {

            // Default true to avoid having to implement method in Layout-s.
            return true;
        }

        giveFocus(): boolean{
            return false;
        }
    }
}