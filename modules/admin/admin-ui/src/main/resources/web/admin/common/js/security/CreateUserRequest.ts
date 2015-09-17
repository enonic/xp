module api.security {

    export class CreateUserRequest extends SecurityResourceRequest<UserJson, User> {

        private key: PrincipalKey;
        private displayName: string;
        private email: string;
        private login: string;
        private password: string;
        private memberships: PrincipalKey[] = [];

        constructor() {
            super();
            super.setMethod("POST");
        }

        setKey(key: PrincipalKey): CreateUserRequest {
            this.key = key;
            return this;
        }

        setDisplayName(displayName: string): CreateUserRequest {
            this.displayName = displayName;
            return this;
        }

        setEmail(email: string): CreateUserRequest {
            this.email = email;
            return this;
        }

        setLogin(login: string): CreateUserRequest {
            this.login = login;
            return this;
        }

        setPassword(password: string): CreateUserRequest {
            this.password = password;
            return this;
        }

        setMemberships(memberships: PrincipalKey[]): CreateUserRequest {
            this.memberships = memberships.slice(0);
            return this;
        }

        getParams(): Object {
            return {
                key: this.key.toString(),
                displayName: this.displayName,
                email: this.email,
                login: this.login,
                password: this.password,
                memberships: this.memberships.map((memberKey) => memberKey.toString())
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'principals', 'createUser');
        }

        sendAndParse(): wemQ.Promise<User> {

            return this.send().then((response: api.rest.JsonResponse<UserJson>) => {
                return User.fromJson(response.getResult());
            });
        }

    }
}