module app_model {

    export interface Authenticator {
        authenticate(userName:string, userStore:UserStore, password:string):boolean;
    }

    export class AuthenticatorImpl implements Authenticator {

        constructor() {
        }

        authenticate(userName:string, userStore:UserStore, password:string):boolean {
            return true; // TODO
        }

    }
}
