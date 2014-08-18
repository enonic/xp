module api.module {

    export class ModuleCache {

        private static instance: ModuleCache;

        private objectsTypesByKey: {[s:string] : Module;} = {};

        constructor() {

            ModuleUpdatedEvent.on((event: ModuleUpdatedEvent) => {
                var moduleKey = event.getModuleKey();
                delete this.objectsTypesByKey[moduleKey.toString()];
            });
        }

        public put(object: Module) {
            console.log("ModuleCache.put: " + object.getModuleKey().toString());
            this.objectsTypesByKey[object.getModuleKey().toString()] = object;
        }

        public getByName(key: ModuleKey): Module {
            console.log("ModuleCache.getByName: " + key.toString());
            return this.objectsTypesByKey[key.toString()];
        }

        static get(): ModuleCache {
            if (!ModuleCache.instance) {
                ModuleCache.instance = new ModuleCache();
            }
            return ModuleCache.instance;
        }
    }
}