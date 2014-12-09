module api.security {

    export class UpdateUserRequest extends SecurityResourceRequest<UserJson, User> {

        private key: PrincipalKey;
        private displayName: string;
        private email: string;
        private login: string;
        private membershipsToAdd: PrincipalKey[] = [];
        private membershipsToRemove: PrincipalKey[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): UpdateUserRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): UpdateUserRequest {
            this.displayName = displayName;
            return this;
        }

        setEmail(email: string): UpdateUserRequest {
            this.email = email;
            return this;
        }

        setLogin(login: string): UpdateUserRequest {
            this.login = login;
            return this;
        }

        addMemberships(memberships: PrincipalKey[]): UpdateUserRequest {
            this.membershipsToAdd = memberships.slice(0);
            return this;
        }

        removeMemberships(memberships: PrincipalKey[]): UpdateUserRequest {
            this.membershipsToRemove = memberships.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                email: this.email,
                login: this.login,
                addMemberships: this.membershipsToAdd.map((memberKey) => memberKey.toString()),
                removeMemberships: this.membershipsToRemove.map((memberKey) => memberKey.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'updateUser');
        }

        sendAndParse(): wemQ.Promise<User> {

            return this.send().then((response: api.rest.JsonResponse<UserJson>) => {
                return User.fromJson(response.getResult());
            });
        }

    }
}