module api.application {

    export class ApplicationCache extends api.cache.Cache<Application, ApplicationKey> {

        private static instance: ApplicationCache;

        constructor() {
            super();

            ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {
                console.log("ApplicationCache on ApplicationUpdatedEvent, deleting: " + event.getApplicationKey().toString());
                this.deleteByKey(event.getApplicationKey());
            });
        }

        copy(object: Application): Application {
            return new ApplicationBuilder(object).build();
        }

        getKeyFromObject(object: Application): ApplicationKey {
            return object.getApplicationKey();
        }

        getKeyAsString(key: ApplicationKey): string {
            return key.toString();
        }

        static get(): ApplicationCache {
            if (!ApplicationCache.instance) {
                ApplicationCache.instance = new ApplicationCache();
            }
            return ApplicationCache.instance;
        }
    }
}