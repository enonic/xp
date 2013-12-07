module api_module {

    export class Module extends api_module.ModuleSummary {

        private config:api_form.Form;

        private moduleDependencies: api_module.ModuleKey[] = [];

        private contentTypeDependencies: api_schema_content.ContentTypeName[] = [];

        private moduleDirectoryEntry: api_module.ModuleFileEntry;

        private minSystemVersion: string;

        private maxSystemVersion: string;

        static fromExtModel(model:Ext_data_Model):Module {
            return new api_module.Module(<api_module_json.ModuleJson>model.raw);
        }

        constructor(json:api_module_json.ModuleJson){
            super(json);

            this.config = json.config != null ? new api_form.Form(json.config) : null;
            this.minSystemVersion = json.minSystemVersion;
            this.maxSystemVersion = json.maxSystemVersion;

            if (json.moduleDependencies != null) {
                json.moduleDependencies.forEach((dependency:string) => {
                    this.moduleDependencies.push(api_module.ModuleKey.fromString(dependency));
                });
            }

            if (json.contentTypeDependencies != null) {
                json.contentTypeDependencies.forEach((dependency:string) => {
                    this.contentTypeDependencies.push(new api_schema_content.ContentTypeName(dependency));
                });
            }

            this.moduleDirectoryEntry = new api_module.ModuleFileEntry(json.moduleDirectoryEntry);
        }

        getForm():api_form.Form {
            return this.config;
        }

        getMinSystemVersion():string {
            return this.minSystemVersion;
        }

        getMaxSystemVersion():string {
            return this.maxSystemVersion;
        }

        getModuleDependencies(): api_module.ModuleKey[] {
            return this.moduleDependencies;
        }

        getContentTypeDependencies(): api_schema_content.ContentTypeName[] {
            return this.contentTypeDependencies;
        }

        getModuleDirectoryEntry(): api_module.ModuleFileEntry {
            return this.moduleDirectoryEntry;
        }
    }
}