module api.schema.mixin {

    export class Mixin extends api.schema.Schema {

        private schemaKey:string;

        private formItems:api.form.FormItem[];

        constructor(mixinJson:api.schema.mixin.json.MixinJson) {
            super(mixinJson);
            this.formItems = [];
            mixinJson.items.forEach((formItemJson:api.form.json.FormItemJson) => {
                this.formItems.push(api.form.FormItemFactory.createFormItem(formItemJson));
            });
            this.schemaKey = "mixin:" + this.getName();
        }

        getMixinName(): MixinName {
            return new MixinName(this.getName());
        }

        getFormItems():api.form.FormItem[] {
            return this.formItems;
        }

        getSchemaKey():string {
            return this.schemaKey;
        }
    }
}