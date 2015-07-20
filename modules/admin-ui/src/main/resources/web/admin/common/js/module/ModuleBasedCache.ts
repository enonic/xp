module api.module {

    export class ModuleBasedCache<CACHE extends api.cache.Cache<any,any>,T,TKEY> {

        private moduleCaches: ModuleCaches<CACHE>;

        constructor() {

            this.moduleCaches = new ModuleCaches<CACHE>();

            ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {

                if (ApplicationUpdatedEventType.STARTED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ApplicationUpdatedEvent STARTED, calling - loadByModule.. " +
                                event.getApplicationKey().toString());
                    this.loadByModule(event.getApplicationKey());
                }
                else if (ApplicationUpdatedEventType.STOPPED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ApplicationUpdatedEvent STOPPED - calling deleteByApplicationKey.. " +
                                event.getApplicationKey().toString());
                    this.deleteByApplicationKey(event.getApplicationKey())
                }
            });
        }

        loadByModule(applicationKey: ApplicationKey) {
            throw new Error("Must be implemented by inheritor");
        }

        getByModule(applicationKey: ApplicationKey): T[] {
            api.util.assertNotNull(applicationKey, "applicationKey not given");
            var cache = this.moduleCaches.getByKey(applicationKey);
            if (!cache) {
                return null;
            }
            return cache.getAll();
        }

        getByKey(key: TKEY, applicationKey: ApplicationKey): T {
            api.util.assertNotNull(key, "key not given");

            var cache = this.moduleCaches.getByKey(applicationKey);
            if (!cache) {
                return null;
            }
            return cache.getByKey(key);
        }

        put(object: T, applicationKey?: ApplicationKey) {
            api.util.assertNotNull(object, "a object to cache must be given");

            var cache = this.moduleCaches.getByKey(applicationKey);
            if (!cache) {
                cache = this.createModuleCache();
                this.moduleCaches.put(applicationKey, cache);
            }
            cache.put(object);
        }

        createModuleCache(): CACHE {
            throw new Error("Must be implemented by inheritor");
        }

        private deleteByApplicationKey(applicationKey: ApplicationKey) {
            this.moduleCaches.removeByKey(applicationKey);
        }
    }
}
