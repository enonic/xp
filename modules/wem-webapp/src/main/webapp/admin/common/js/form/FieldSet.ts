module api.form{

    export class FieldSet extends Layout {

        private label:string;

        private formItems:FormItem[] = [];

        constructor(fieldSetJson:api.form.json.FieldSetJson) {
            super(fieldSetJson.name);
            this.label = fieldSetJson.label;

            if (fieldSetJson.items != null) {
                fieldSetJson.items.forEach((formItemJson:api.form.json.FormItemJson) => {
                    this.addFormItem(FormItemFactory.createFormItem(formItemJson));
                });
            }
        }

        addFormItem(formItem:FormItem) {
            this.formItems.push(formItem);
        }

        getLabel():string {
            return this.label;
        }

        getFormItems():FormItem[] {
            return this.formItems;
        }

        public toFieldSetJson():api.form.json.FormItemTypeWrapperJson {

            return <api.form.json.FormItemTypeWrapperJson>{ FieldSet: <api.form.json.FieldSetJson>{
                name: this.getName(),
                items : FormItem.formItemsToJson(this.getFormItems()),
                label : this.getLabel()
            }};
        }
    }
}