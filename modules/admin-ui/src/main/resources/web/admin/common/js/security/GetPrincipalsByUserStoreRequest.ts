module api.security {

    export class GetPrincipalsByUserStoreRequest extends SecurityResourceRequest<PrincipalListJson, Principal[]> {

        private userStore: UserStoreKey;

        private principalTypes: PrincipalType[];

        constructor(userStore: UserStoreKey, principalTypes: PrincipalType[]) {
            super();
            super.setMethod("GET");
            this.userStore = userStore;
            this.principalTypes = principalTypes;
        }

        getParams(): Object {
            return {
                'userStoreKey': this.userStore.getId(),
                'types': this.getType()

            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals');
        }

        private getType(): string {
            var typeStr: string = "";
            this.principalTypes.forEach((type) => {
                typeStr += PrincipalType[type];
                typeStr += ",";
            });
            return typeStr.substr(0, typeStr.length-1);
        }

        sendAndParse(): wemQ.Promise<Principal[]> {

            return this.send().then((response: api.rest.JsonResponse<PrincipalListJson>) => {
                return response.getResult().principals.map((principalJson: PrincipalJson) => {
                    return this.fromJsonToPrincipal(principalJson);
                });
            });
        }

    }
}