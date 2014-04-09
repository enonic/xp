module api.module {

    export class Module extends api.module.ModuleSummary {

        private config:api.form.Form;

        private moduleDependencies: api.module.ModuleKey[] = [];

        private contentTypeDependencies: api.schema.content.ContentTypeName[] = [];

        private minSystemVersion: string;

        private maxSystemVersion: string;

        static fromExtModel(model:Ext_data_Model):Module {
            return new api.module.Module(<api.module.json.ModuleJson>model.raw);
        }

        static fromJsonArray(jsonArray:api.module.json.ModuleJson[]):Module[] {
            var array:Module[] = [];
            jsonArray.forEach((json:api.module.json.ModuleJson) => {
                array.push(new Module(json));
            });
            return array;
        }

        constructor(json:api.module.json.ModuleJson){
            super(json);

            this.config = json.config != null ? new api.form.Form(json.config) : null;
            this.minSystemVersion = json.minSystemVersion;
            this.maxSystemVersion = json.maxSystemVersion;

            if (json.moduleDependencies != null) {
                json.moduleDependencies.forEach((dependency:string) => {
                    this.moduleDependencies.push(api.module.ModuleKey.fromString(dependency));
                });
            }

            if (json.contentTypeDependencies != null) {
                json.contentTypeDependencies.forEach((dependency:string) => {
                    this.contentTypeDependencies.push(new api.schema.content.ContentTypeName(dependency));
                });
            }
        }

        getForm():api.form.Form {
            return this.config;
        }

        getMinSystemVersion():string {
            return this.minSystemVersion;
        }

        getMaxSystemVersion():string {
            return this.maxSystemVersion;
        }

        getModuleDependencies(): api.module.ModuleKey[] {
            return this.moduleDependencies;
        }

        getContentTypeDependencies(): api.schema.content.ContentTypeName[] {
            return this.contentTypeDependencies;
        }
    }
}