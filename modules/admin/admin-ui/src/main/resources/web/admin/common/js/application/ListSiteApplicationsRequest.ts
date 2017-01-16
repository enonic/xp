module api.application {

    export class ListSiteApplicationsRequest extends ListApplicationsRequest {

        constructor() {
            super("getSiteApplications");
        }

    }
}
