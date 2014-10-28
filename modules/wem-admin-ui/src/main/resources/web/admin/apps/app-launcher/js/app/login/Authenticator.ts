module app.login {

    import UserStore = api.security.UserStore;

    export interface Authenticator {
        authenticate(userName: string, userStore: UserStore, password: string):boolean;
    }

    export class AuthenticatorImpl implements Authenticator {

        constructor() {
        }

        authenticate(userName: string, userStore: UserStore, password: string): boolean {
            return true; // TODO
        }

    }
}
