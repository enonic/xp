module api.module {

    export class ModuleCache extends api.cache.Cache<Module, ModuleKey> {

        private static instance: ModuleCache;

        constructor() {
            super();

            ModuleUpdatedEvent.on((event: ModuleUpdatedEvent) => {
                console.log("ModuleCache on ModuleUpdatedEvent, deleting: " + event.getModuleKey().toString());
                this.deleteByKey(event.getModuleKey());
            });
        }

        copy(object: Module): Module {
            return new ModuleBuilder(object).build();
        }

        getKeyFromObject(object: Module): ModuleKey {
            return object.getModuleKey();
        }

        getKeyAsString(key: ModuleKey): string {
            return key.toString();
        }

        static get(): ModuleCache {
            if (!ModuleCache.instance) {
                ModuleCache.instance = new ModuleCache();
            }
            return ModuleCache.instance;
        }
    }
}