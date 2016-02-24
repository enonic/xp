module api.security {

    export class CreatePathGuardRequest extends SecurityResourceRequest<PathGuardJson, PathGuard> {

        private key: string;
        private displayName: string;
        private authConfig: UserStoreAuthConfig;
        private paths: string[];

        constructor() {
            super();
            super.setMethod("POST");
        }

        getParams(): Object {
            return {
                key: this.key,
                displayName: this.displayName,
                authConfig: this.authConfig ? this.authConfig.toJson() : undefined,
                paths: this.paths ? this.paths : []
            };
        }

        setKey(key: string): CreatePathGuardRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): CreatePathGuardRequest {
            this.displayName = displayName;
            return this;
        }

        setAuthConfig(authConfig: UserStoreAuthConfig): CreatePathGuardRequest {
            this.authConfig = authConfig;
            return this;
        }

        setPaths(paths: string[]): CreatePathGuardRequest {
            this.paths = paths;
            return this;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'pathguard/create');
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