module api.ui.security.auth {

    export class AuthServiceViewer extends api.ui.NamesAndIconViewer<api.security.auth.AuthService> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.security.auth.AuthService): string {
            return object.getDisplayName();
        }

        resolveSubName(object: api.security.auth.AuthService, relativePath: boolean = false): string {
            return object.getKey();
        }

        resolveIconClass(object: api.security.auth.AuthService): string {
            return "icon-shield icon-large";
        }
    }
}