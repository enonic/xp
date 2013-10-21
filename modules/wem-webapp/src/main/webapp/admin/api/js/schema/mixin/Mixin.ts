module api_schema_mixin {

    export class Mixin extends api_item.BaseItem {

        private schemaKey:string;

        private name:string;

        private displayName:string;

        private qualifiedName:string;

        private formItems:api_schema_content_form.FormItem[];

        private icon:string;

        constructor(json:api_schema_mixin_json.MixinJson) {
            super(json);
            this.name = json.name;
            this.displayName = json.displayName;
            this.qualifiedName = this.name;
            this.formItems = [];
            json.items.forEach((item:api_schema_content_form_json.FormItemJson) => {
                this.formItems.push(new api_schema_content_form[item.formItemType](item));
            });
            this.icon = json.iconUrl;
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

        getFormItems():api_schema_content_form.FormItem[] {
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