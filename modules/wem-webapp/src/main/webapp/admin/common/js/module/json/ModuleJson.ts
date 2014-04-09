module api.module.json{

    export interface ModuleJson extends ModuleSummaryJson {

        config: api.form.json.FormJson;

        moduleDependencies: string[];

        contentTypeDependencies: string[];

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}