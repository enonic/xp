module api.security.auth {

    import BaseLoader = api.util.loader.BaseLoader;
    import ApplicationListResult = api.application.ApplicationListResult;
    import Application = api.application.Application;

    export class AuthApplicationLoader extends BaseLoader<ApplicationListResult, Application> {

        constructor() {
            super(new api.application.ListAuthApplicationsRequest());
        }

        filterFn(application: api.application.Application) {
            return application.getDisplayName().toString().toLowerCase().indexOf(this.getSearchString().toLowerCase()) !== -1;
        }
    }
}
