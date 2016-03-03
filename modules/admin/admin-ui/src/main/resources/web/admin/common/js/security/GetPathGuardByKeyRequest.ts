module api.security {

    export class GetPathGuardByKeyRequest extends SecurityResourceRequest<PathGuardJson, PathGuard> {

        private key: api.security.PathGuardKey;

        constructor(key: api.security.PathGuardKey) {
            super();
            super.setMethod("GET");
            this.key = key;
        }

        getParams(): Object {
            return {
                key: this.key.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard');
        }

        sendAndParse(): wemQ.Promise<PathGuard> {
            return this.send().then((response: api.rest.JsonResponse<PathGuardJson>) => {
                return this.fromJsonToPathGuard(response.getResult());
            });
        }

        fromJsonToPathGuard(json: PathGuardJson): PathGuard {
            return PathGuard.fromJson(json);
        }
    }
}