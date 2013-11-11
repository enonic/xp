module api_content_site{

    export class ModuleConfig {

        private moduleKey:api_module.ModuleKey;

        private config:api_data.RootDataSet;

        constructor(moduleConfigJson:api_content_site_json.ModuleConfigJson) {
            this.moduleKey = moduleConfigJson.moduleKey;
            this.config = api_data.DataFactory.createRootDataSet( moduleConfigJson.config );
        }
    }
}