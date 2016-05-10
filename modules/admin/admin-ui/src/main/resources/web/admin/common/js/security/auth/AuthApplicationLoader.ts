module api.security.auth {

    export class AuthApplicationLoader extends api.util.loader.BaseLoader<api.application.ApplicationListResult, api.application.Application> {

        constructor() {
            super(new api.application.ListAuthApplicationsRequest());
        }

        filterFn(application: api.application.Application) {
            return application.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) != -1;
        }
    }
}