module api.security {

    export class UpdateUserPasswordRequest extends SecurityResourceRequest<UserJson, User> {

        private key: PrincipalKey;
        private password: string;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): UpdateUserPasswordRequest {
            this.key = key;
            return this;
        }

        setPassword(password: string): UpdateUserPasswordRequest {
            this.password = password;
            return this;
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                password: this.password
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'updatePassword');
        }

        sendAndParse(): wemQ.Promise<User> {

            return this.send().then((response: api.rest.JsonResponse<UserJson>) => {
                return User.fromJson(response.getResult());
            });
        }

    }
}