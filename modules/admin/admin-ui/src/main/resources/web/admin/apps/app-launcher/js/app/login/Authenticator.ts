module app.login {

    export interface Authenticator {
        authenticate(userName: string, password: string,
                     authHandler: (loginResult: api.security.auth.LoginResult) => void);
    }

    export class AuthenticatorImpl implements Authenticator {

        constructor() {
        }

        authenticate(userName: string, password: string,
                     authHandler: (loginResult: api.security.auth.LoginResult) => void) {
            var loginCredentials = new api.security.auth.LoginCredentials().
                setUser(userName).
                setPassword(password);

            new api.security.auth.LoginRequest(loginCredentials).sendAndParse().then((loginResult) => {
                authHandler(loginResult);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

    }
}
