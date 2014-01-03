module api.module.json{

    export interface ModuleJson extends ModuleSummaryJson {

        config: api.form.json.FormJson;

        moduleDependencies: string[];

        contentTypeDependencies: string[];

        moduleDirectoryEntry: api.module.json.ModuleFileEntryJson;

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}