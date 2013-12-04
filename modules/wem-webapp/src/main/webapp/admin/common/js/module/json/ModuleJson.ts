module api_module_json{

    export interface ModuleJson extends ModuleSummaryJson {

        config: api_form_json.FormJson;

        moduleDependencies: string[];

        contentTypeDependencies: string[];

        moduleDirectoryEntry: api_module_json.ModuleFileEntryJson;

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}