module api_schema_mixin {

    export class Mixin {

        private name:string;
        private moduleName:string;
        private displayName:string;
        private qualifiedName:string;
        private formItem:api_schema_content_form.FormItem;
        private createdTime:Date;
        private modifiedTime:Date;
        private icon:string;
        private schemaKey:string;


        constructor(json:api_schema_mixin_json.MixinJson) {
            this.name = json.name;
            this.moduleName = json.module;
            this.displayName = json.displayName;
            this.qualifiedName = this.moduleName + ":" + this.name;
            if (json.formItemSet) {
                this.formItem = new api_schema_content_form.FormItemSet(json.formItemSet);
            } else if (json.layout) {
                this.formItem = new api_schema_content_form.Layout(json.layout.name);
            } else if (json.input) {
                this.formItem = new api_schema_content_form.Input(json.input);
            }
            this.icon = json.iconUrl;
            this.schemaKey = "mixin:" + this.qualifiedName;
        }


        getName():string {
            return this.name;
        }

        getModuleName():string {
            return this.moduleName;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getQualifiedName():string {
            return this.qualifiedName;
        }

        getFormItem():api_schema_content_form.FormItem {
            return this.formItem;
        }

        getCreatedTime():Date {
            return this.createdTime;
        }

        getModifiedTime():Date {
            return this.modifiedTime;
        }

        getIcon():string {
            return this.icon;
        }

        getSchemaKey():string {
            return this.schemaKey;
        }
    }
}