module api.application {

    import ApplicationJson = api.application.json.ApplicationJson;

    export class GetApplicationRequest extends ApplicationResourceRequest<ApplicationJson, Application> {

        private applicationKey: ApplicationKey;

        private skipCache: boolean;

        constructor(applicationKey: ApplicationKey, skipCache: boolean = false) {
            super();
            super.setMethod('GET');
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

            let cache = ApplicationCache.get();
            let appObj = this.skipCache ? null : cache.getByKey(this.applicationKey);
            if (appObj) {
                return wemQ(appObj);
            } else {
                return this.send().then((response: api.rest.JsonResponse<ApplicationJson>) => {
                    appObj = this.fromJsonToApplication(response.getResult());
                    cache.put(appObj);
                    return appObj;
                });
            }
        }
    }
}
