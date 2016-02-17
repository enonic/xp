module api.security {

    export class ListPathGuardsRequest extends api.security.SecurityResourceRequest<PathGuardListJson, PathGuard[]> {

        constructor() {
            super();
        }

        getParams(): Object {
            return {}
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard/list');
        }

        sendAndParse(): wemQ.Promise<PathGuard[]> {
            return this.send().
                then((response: api.rest.JsonResponse<PathGuardListJson>) => {
                    return response.getResult().
                        pathGuards.
                        map((pathGuardJson: PathGuardJson) => PathGuard.fromJson(pathGuardJson));
                });
        }
    }
}