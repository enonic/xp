module api.module {

    export class ModuleBasedCache<CACHE extends api.cache.Cache<any,any>,T,TKEY> {

        private moduleCaches: ModuleCaches<CACHE>;

        constructor() {

            this.moduleCaches = new ModuleCaches<CACHE>();

            ModuleUpdatedEvent.on((event: ModuleUpdatedEvent) => {

                if (ModuleUpdatedEventType.STARTED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ModuleUpdatedEvent STARTED, calling - loadByModule.. " +
                                event.getModuleKey().toString());
                    this.loadByModule(event.getModuleKey());
                }
                else if (ModuleUpdatedEventType.STOPPED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ModuleUpdatedEvent STOPPED - calling deleteByModuleKey.. " +
                                event.getModuleKey().toString());
                    this.deleteByModuleKey(event.getModuleKey())
                }
            });
        }

        loadByModule(moduleKey: ModuleKey) {
            throw new Error("Must be implemented by inheritor");
        }

        getByModule(moduleKey: ModuleKey): T[] {
            api.util.assertNotNull(moduleKey, "moduleKey not given");
            var cache = this.moduleCaches.getByKey(moduleKey);
            if (!cache) {
                return null;
            }
            return cache.getAll();
        }

        getByKey(key: TKEY, moduleKey: ModuleKey): T {
            api.util.assertNotNull(key, "key not given");

            var cache = this.moduleCaches.getByKey(moduleKey);
            if (!cache) {
                return null;
            }
            return cache.getByKey(key);
        }

        put(object: T, moduleKey?: ModuleKey) {
            api.util.assertNotNull(object, "a object to cache must be given");

            var cache = this.moduleCaches.getByKey(moduleKey);
            if (!cache) {
                cache = this.createModuleCache();
                this.moduleCaches.put(moduleKey, cache);
            }
            cache.put(object);
        }

        createModuleCache(): CACHE {
            throw new Error("Must be implemented by inheritor");
        }

        private deleteByModuleKey(moduleKey: ModuleKey) {
            this.moduleCaches.removeByKey(moduleKey);
        }
    }
}
