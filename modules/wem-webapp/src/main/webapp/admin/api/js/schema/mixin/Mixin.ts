module api_schema_mixin {

    export class Mixin extends api_item.BaseItem {

        private schemaKey:string;

        private name:string;

        private displayName:string;

        private qualifiedName:string;

        private formItems:api_form.FormItem[];

        private icon:string;

        constructor(mixinJson:api_schema_mixin_json.MixinJson) {
            super(mixinJson);
            this.name = mixinJson.name;
            this.displayName = mixinJson.displayName;
            this.qualifiedName = this.name;
            this.formItems = [];
            mixinJson.items.forEach((formItemJson:api_form_json.FormItemJson) => {
                this.formItems.push(api_form.FormItemFactory.createFormItem(formItemJson));
            });
            this.icon = mixinJson.iconUrl;
            this.schemaKey = "mixin:" + this.qualifiedName;
        }


        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getQualifiedName():string {
            return this.qualifiedName;
        }

        getFormItems():api_form.FormItem[] {
            return this.formItems;
        }

        getIcon():string {
            return this.icon;
        }

        getSchemaKey():string {
            return this.schemaKey;
        }
    }
}