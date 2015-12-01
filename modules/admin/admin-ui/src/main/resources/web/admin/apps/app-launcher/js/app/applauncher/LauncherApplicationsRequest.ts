module app.launcher {

    export class LauncherApplicationsRequest extends app.launcher.LauncherResourceRequest<app.launcher.json.AdminApplicationJson[], api.app.Application[]> {

        constructor() {
            super();
            super.setMethod("GET");
        }

        getParams(): Object {
            return {}
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "apps");
        }

        sendAndParse(): wemQ.Promise<api.app.Application[]> {
            return this.send().then((response: api.rest.JsonResponse<app.launcher.json.AdminApplicationJson[]>) => {
                var array: api.app.Application[] = [];
                response.getResult().forEach((json: app.launcher.json.AdminApplicationJson) => {
                    array.push(this.fromJsonToApplication(json));
                });
                return array;
            });
        }
    }
}