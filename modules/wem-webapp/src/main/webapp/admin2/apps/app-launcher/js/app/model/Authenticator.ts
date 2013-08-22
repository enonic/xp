module app_model {

    export interface Authenticator {
        authenticate(userName:string, userStore:UserStore, password:string):bool;
    }

    export class AuthenticatorImpl implements Authenticator {

        constructor() {
        }

        authenticate(userName:string, userStore:UserStore, password:string):bool {
            return true; // TODO
        }

    }
}
