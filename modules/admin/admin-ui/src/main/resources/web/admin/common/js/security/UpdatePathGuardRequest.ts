module api.security {

    export class UpdatePathGuardRequest extends SecurityResourceRequest<PathGuardJson, PathGuard> {

        private key: PathGuardKey;
        private displayName: string;
        private authConfig: AuthConfig;
        private paths: string[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                authConfig: this.authConfig ? this.authConfig.toJson() : undefined,
                paths: this.paths ? this.paths : []
            };
        }

        setKey(key: PathGuardKey): UpdatePathGuardRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): UpdatePathGuardRequest {
            this.displayName = displayName;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): UpdatePathGuardRequest {
            this.authConfig = authConfig;
            return this;
        }

        setPaths(paths: string[]): UpdatePathGuardRequest {
            this.paths = paths;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard/update');
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