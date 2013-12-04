module api_module_json{

    export interface ModuleFileEntryJson {

        name: string;

        resource: string;

        entries: ModuleFileEntryJson[];
    }
}