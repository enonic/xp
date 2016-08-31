module api.application {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class SiteApplicationLoader extends api.util.loader.BaseLoader<ApplicationListResult, Application> {

        private listSiteApplicationsRequest: ListSiteApplicationsRequest;

        private filterObject: Object;

        constructor(filterObject: Object = null) {
            super(this.listSiteApplicationsRequest = new ListSiteApplicationsRequest());
            if (filterObject) {
                this.filterObject = filterObject;
            }
        }

        load(): wemQ.Promise<Application[]> {
            var me = this;
            me.notifyLoadingData();

            return me.sendRequest()
                .then((applications: Application[]) => {
                    if (me.filterObject) {
                        applications = applications.filter(me.filterResults, me);
                    }
                    me.notifyLoadedData(applications);

                    return applications;
                });
        }

        private filterResults(application: Application): boolean {
            if (!this.filterObject) {
                return true;
            }

            var result = true;
            for (var name in this.filterObject) {
                if (this.filterObject.hasOwnProperty(name)) {
                    if (!application.hasOwnProperty(name) || this.filterObject[name] != application[name]) {
                        result = false;
                    }
                }
            }

            return result;
        }

    }
}