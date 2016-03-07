module api.security {

    export class CreatePathGuardRequest extends SecurityResourceRequest<PathGuardJson, PathGuard> {

        private key: PathGuardKey;
        private displayName: string;
        private description: string;
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
                description: this.description,
                authConfig: this.authConfig ? this.authConfig.toJson() : undefined,
                paths: this.paths ? this.paths : []
            };
        }

        setKey(key: PathGuardKey): CreatePathGuardRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): CreatePathGuardRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): CreatePathGuardRequest {
            this.description = description;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): CreatePathGuardRequest {
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