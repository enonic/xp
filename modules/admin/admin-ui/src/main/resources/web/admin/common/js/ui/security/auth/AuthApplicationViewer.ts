module api.ui.security.auth {

    export class AuthApplicationViewer extends api.ui.NamesAndIconViewer<api.application.Application> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.application.Application): string {
            return object.getDisplayName();
        }

        resolveSubName(object: api.application.Application, relativePath: boolean = false): string {
            return object.getApplicationKey().toString();
        }

        resolveIconClass(object: api.application.Application): string {
            return "icon-shield icon-large";
        }
    }
}