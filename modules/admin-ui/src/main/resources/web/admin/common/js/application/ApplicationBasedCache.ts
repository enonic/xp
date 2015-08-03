module api.application {

    export class ApplicationBasedCache<CACHE extends api.cache.Cache<any,any>,T,TKEY> {

        private applicationCaches: ApplicationCaches<CACHE>;

        constructor() {

            this.applicationCaches = new ApplicationCaches<CACHE>();

            ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {

                if (ApplicationUpdatedEventType.STARTED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) +
                                " received ApplicationUpdatedEvent STARTED, calling - loadByApplication.. " +
                                event.getApplicationKey().toString());
                    this.loadByApplication(event.getApplicationKey());
                }
                else if (ApplicationUpdatedEventType.STOPPED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ApplicationUpdatedEvent STOPPED - calling deleteByApplicationKey.. " +
                                event.getApplicationKey().toString());
                    this.deleteByApplicationKey(event.getApplicationKey())
                }
            });
        }

        loadByApplication(applicationKey: ApplicationKey) {
            throw new Error("Must be implemented by inheritor");
        }

        getByApplication(applicationKey: ApplicationKey): T[] {
            api.util.assertNotNull(applicationKey, "applicationKey not given");
            var cache = this.applicationCaches.getByKey(applicationKey);
            if (!cache) {
                return null;
            }
            return cache.getAll();
        }

        getByKey(key: TKEY, applicationKey: ApplicationKey): T {
            api.util.assertNotNull(key, "key not given");

            var cache = this.applicationCaches.getByKey(applicationKey);
            if (!cache) {
                return null;
            }
            return cache.getByKey(key);
        }

        put(object: T, applicationKey?: ApplicationKey) {
            api.util.assertNotNull(object, "a object to cache must be given");

            var cache = this.applicationCaches.getByKey(applicationKey);
            if (!cache) {
                cache = this.createApplicationCache();
                this.applicationCaches.put(applicationKey, cache);
            }
            cache.put(object);
        }

        createApplicationCache(): CACHE {
            throw new Error("Must be implemented by inheritor");
        }

        private deleteByApplicationKey(applicationKey: ApplicationKey) {
            this.applicationCaches.removeByKey(applicationKey);
        }
    }
}
