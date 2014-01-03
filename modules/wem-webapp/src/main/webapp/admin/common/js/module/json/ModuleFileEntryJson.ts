module api.module.json{

    export interface ModuleFileEntryJson {

        name: string;

        resource: string;

        entries: ModuleFileEntryJson[];
    }
}