module api.module{

    export class ModuleFileEntry {

        private name: string;

        private resource: string;

        private entries: api.module.ModuleFileEntry[] = [];

        constructor(json:api.module.json.ModuleFileEntryJson) {
            this.name = json.name;
            this.resource = json.resource;

            if (json.entries != null) {
                json.entries.forEach((entryJson:api.module.json.ModuleFileEntryJson) => {
                    this.entries.push(new api.module.ModuleFileEntry(entryJson));
                });
            }
        }

        getName():string {
            return this.name;
        }

        getResource():string {
            return this.resource;
        }

        getEntries():api.module.ModuleFileEntry[] {
            return this.entries;
        }
    }
}