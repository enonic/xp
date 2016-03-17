module api.security {

    export class UpdatePathGuardRequest extends SecurityResourceRequest<PathGuardJson, PathGuard> {

        private key: PathGuardKey;
        private displayName: string;
        private description: string;
        private userStoreKey: UserStoreKey;
        private passive: boolean;
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
                userStoreKey: this.userStoreKey ? this.userStoreKey.getId() : null,
                passive: this.passive,
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

        setDescription(description: string): UpdatePathGuardRequest {
            this.description = description;
            return this;
        }

        setUserStoreKey(userStoreKey: UserStoreKey): UpdatePathGuardRequest {
            this.userStoreKey = userStoreKey;
            return this;
        }

        setPassive(passive: boolean): UpdatePathGuardRequest {
            this.passive = passive;
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