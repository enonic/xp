module api.ui.security.auth {

    export class AuthApplicationLoader extends api.util.loader.BaseLoader<api.application.ApplicationListResult, api.application.Application> {

        constructor() {
            super(new api.application.ListAuthApplicationsRequest());
        }
    }
}