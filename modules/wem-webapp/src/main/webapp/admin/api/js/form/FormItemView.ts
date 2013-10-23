module api_form {

    export class FormItemView extends api_dom.DivEl {

        private formItem:FormItem;

        constructor(idPrefix:string, className:string, formItem:FormItem) {
            super(idPrefix, className);

            this.formItem = formItem;
        }

        getFormItem():FormItem {
            return this.formItem;
        }

        getData():api_data.Data[] {
            throw new Error("Method needs to be implemented in inheritor");
        }

        validate(validationRecorder:ValidationRecorder) {

            // Default method to avoid having to implement method in Layout-s.
        }

        hasValidOccurrences():boolean {

            // Default true to avoid having to implement method in Layout-s.
            return true;
        }
    }
}