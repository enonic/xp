module api.ui.security.auth {
    export class AuthServiceLoader extends api.util.loader.BaseLoader<api.security.auth.AuthServiceJson[], api.security.auth.AuthService> {
        constructor() {
            super(new api.security.auth.AuthServicesRequest());
        }
    }
}
