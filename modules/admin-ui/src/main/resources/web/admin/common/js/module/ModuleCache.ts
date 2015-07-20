module api.module {

    export class ModuleCache extends api.cache.Cache<Application, ApplicationKey> {

        private static instance: ModuleCache;

        constructor() {
            super();

            ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {
                console.log("ModuleCache on ApplicationUpdatedEvent, deleting: " + event.getApplicationKey().toString());
                this.deleteByKey(event.getApplicationKey());
            });
        }

        copy(object: Application): Application {
            return new ModuleBuilder(object).build();
        }

        getKeyFromObject(object: Application): ApplicationKey {
            return object.getApplicationKey();
        }

        getKeyAsString(key: ApplicationKey): string {
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