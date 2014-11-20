module api.security {

    export class UpdateUserRequest extends SecurityResourceRequest<UserJson, User> {

        private key: PrincipalKey;
        private displayName: string;
        private email: string;
        private login: string;

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

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                email: this.email,
                login: this.login
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