module api.application {

    import ApplicationInstallResultJson = api.application.json.ApplicationInstallResultJson;

    export class InstallUrlApplicationRequest extends ApplicationResourceRequest<ApplicationInstallResultJson, ApplicationInstallResult> {

        private applicationUrl: string;

        constructor(applicationUrl: string) {
            super();
            super.setMethod("POST");
            this.applicationUrl = applicationUrl;
            this.setHeavyOperation(true);
        }

        getParams(): Object {
            return {
                URL: this.applicationUrl
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "installUrl");
        }

        sendAndParse(): wemQ.Promise<ApplicationInstallResult> {
            return this.send().then((response: api.rest.JsonResponse<ApplicationInstallResultJson>) => {
                return ApplicationInstallResult.fromJson(response.getResult());
            });
        }
    }
}