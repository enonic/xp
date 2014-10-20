module api.module {

    export class ModuleCaches<CACHE extends api.cache.Cache<any,any>> {

        private cacheByModuleKey: {[s:string] : CACHE;} = {};

        put(key: ModuleKey, cache: CACHE) {
            this.cacheByModuleKey[key.toString()] = cache;
        }

        getByKey(key: ModuleKey): CACHE {
            return this.cacheByModuleKey[key.toString()];
        }

        removeByKey(key: ModuleKey) {
            delete this.cacheByModuleKey[key.toString()];
        }
    }

}