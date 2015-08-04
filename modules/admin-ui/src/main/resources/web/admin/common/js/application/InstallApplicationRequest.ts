module api.application {

    export class InstallApplicationRequest extends ApplicationResourceRequest<api.item.ItemJson, void> {

        private applicationUrl: string;

        constructor(applicationUrl: string) {
            super();
            super.setMethod("POST");
            this.applicationUrl = applicationUrl;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "install");
        }

        getParams(): Object {
            return {
                url: this.applicationUrl
            };
        }

        sendAndParse(): wemQ.Promise<void> {

            return this.send().then((response: api.rest.JsonResponse<api.item.ItemJson>) => {

            });
        }
    }
}