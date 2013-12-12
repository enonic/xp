module api_module{

    export class ModuleFileEntry {

        private name: string;

        private resource: string;

        private entries: api_module.ModuleFileEntry[] = [];

        constructor(json:api_module_json.ModuleFileEntryJson) {
            this.name = json.name;
            this.resource = json.resource;

            if (json.entries != null) {
                json.entries.forEach((entryJson:api_module_json.ModuleFileEntryJson) => {
                    this.entries.push(new api_module.ModuleFileEntry(entryJson));
                });
            }
        }

        getName():string {
            return this.name;
        }

        getResource():string {
            return this.resource;
        }

        getEntries():api_module.ModuleFileEntry[] {
            return this.entries;
        }
    }
}