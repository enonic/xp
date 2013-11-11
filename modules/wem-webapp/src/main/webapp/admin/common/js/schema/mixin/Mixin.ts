module api_schema_mixin {

    export class Mixin extends api_schema.Schema {

        private schemaKey:string;

        private formItems:api_form.FormItem[];

        constructor(mixinJson:api_schema_mixin_json.MixinJson) {
            super(mixinJson);
            this.formItems = [];
            mixinJson.items.forEach((formItemJson:api_form_json.FormItemJson) => {
                this.formItems.push(api_form.FormItemFactory.createFormItem(formItemJson));
            });
            this.schemaKey = "mixin:" + this.getName();
        }

        getFormItems():api_form.FormItem[] {
            return this.formItems;
        }

        getSchemaKey():string {
            return this.schemaKey;
        }
    }
}