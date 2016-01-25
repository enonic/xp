module api.application {

    export class InstallUrlApplicationRequest extends ApplicationResourceRequest<json.ApplicationJson, Application> {

        private applicationUrl: string;

        constructor(applicationUrl: string) {
            super();
            super.setMethod("POST");
            this.applicationUrl = applicationUrl;
        }

        getParams(): Object {
            return {
                URL: this.applicationUrl
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "installUrl");
        }

        sendAndParse(): wemQ.Promise<Application> {
            return this.send().then((response: api.rest.JsonResponse<json.ApplicationJson>) => {
                return this.fromJsonToApplication(response.getResult());
            });
        }
    }
}