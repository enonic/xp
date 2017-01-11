module api.application {

    import ApplicationJson = api.application.json.ApplicationJson;
    export class AuthApplicationRequest extends ApplicationResourceRequest<ApplicationJson, Application> {

        private applicationKey: ApplicationKey;

        constructor(applicationKey: ApplicationKey) {
            super();
            super.setMethod("GET");

            this.applicationKey = applicationKey;
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getIdProvider");
        }

        sendAndParse(): wemQ.Promise<Application> {
            return this.send().then((response: api.rest.JsonResponse<ApplicationJson>) => {
                return response.getResult() ? Application.fromJson(response.getResult()) : null;
            });
        }
    }
}
