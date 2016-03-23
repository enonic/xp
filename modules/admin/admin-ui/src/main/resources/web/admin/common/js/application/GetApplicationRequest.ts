module api.application {

    export class GetApplicationRequest extends ApplicationResourceRequest<json.ApplicationJson, Application> {

        private applicationKey: ApplicationKey;

        private skipCache: boolean;

        constructor(applicationKey: ApplicationKey, skipCache: boolean = false) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
            this.skipCache = skipCache;
            this.setHeavyOperation(true);
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath());
        }

        sendAndParse(): wemQ.Promise<Application> {

            var cache = ApplicationCache.get();
            var appObj = this.skipCache ? null : cache.getByKey(this.applicationKey);
            if (appObj) {
                return wemQ(appObj);
            }
            else {
                return this.send().then((response: api.rest.JsonResponse<json.ApplicationJson>) => {
                    appObj = this.fromJsonToApplication(response.getResult());
                    cache.put(appObj);
                    return appObj;
                });
            }
        }
    }
}