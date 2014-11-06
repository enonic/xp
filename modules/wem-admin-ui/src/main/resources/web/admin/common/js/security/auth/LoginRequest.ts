module api.security.auth {

    export class LoginRequest extends AuthResourceRequest<LoginResultJson, LoginResult> {

        private loginCredentials: LoginCredentials;

        constructor(loginCredentials: LoginCredentials) {
            super();
            super.setMethod("POST");
            this.loginCredentials = loginCredentials;
        }

        getParams(): Object {
            return {
                user: this.loginCredentials.getUser(),
                password: this.loginCredentials.getPassword(),
                rememberMe: this.loginCredentials.isRememberMe()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'login');
        }

        sendAndParse(): wemQ.Promise<LoginResult> {

            return this.send().then((response: api.rest.JsonResponse<LoginResultJson>) => {
                return new LoginResult(response.getResult());
            });
        }

    }
}