module api.application {

    export class ApplicationCaches<CACHE extends api.cache.Cache<any,any>> {

        private cacheByApplicationKey: {[s:string] : CACHE;} = {};

        put(key: ApplicationKey, cache: CACHE) {
            this.cacheByApplicationKey[key.toString()] = cache;
        }

        getByKey(key: ApplicationKey): CACHE {
            return this.cacheByApplicationKey[key.toString()];
        }

        removeByKey(key: ApplicationKey) {
            delete this.cacheByApplicationKey[key.toString()];
        }
    }

}