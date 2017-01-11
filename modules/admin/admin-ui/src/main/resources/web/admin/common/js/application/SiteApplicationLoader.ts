module api.application {

    export class SiteApplicationLoader extends ApplicationLoader {

        constructor(filterObject: Object) {
            super(filterObject, new ListSiteApplicationsRequest());
        }
    }
}
