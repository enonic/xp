module app.login {

    import UserStore = api.security.UserStore;

    export interface Authenticator {
        authenticate(userName: string, userStore: UserStore, password: string,
                     authHandler: (loginResult: api.security.auth.LoginResult) => void);
    }

    export class AuthenticatorImpl implements Authenticator {

        constructor() {
        }

        authenticate(userName: string, userStore: UserStore, password: string,
                     authHandler: (loginResult: api.security.auth.LoginResult) => void) {
            var loginCredentials = new api.security.auth.LoginCredentials().
                setUser(userName).
                setUserStore(userStore.getKey()).
                setPassword(password);

            new api.security.auth.LoginRequest(loginCredentials).sendAndParse().then((loginResult) => {
                authHandler(loginResult);
            });
        }

    }
}
