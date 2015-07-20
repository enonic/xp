module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<ModuleListResult, Application> {

        private listModulesRequest: ListModulesRequest;

        private filterObject: Object;

        constructor(delay: number = 500, filterObject: Object = null) {
            super(this.listModulesRequest = new ListModulesRequest());
            if (filterObject) {
                this.filterObject = filterObject;
            }
        }

        search(searchString: string): wemQ.Promise<Application[]> {
            this.listModulesRequest.setSearchQuery(searchString);
            return this.load();
        }

        load(): wemQ.Promise<Application[]> {
            var me = this;
            me.notifyLoadingData();

            return me.sendRequest()
                .then((modules: Application[]) => {
                    if (me.filterObject) {
                        modules = modules.filter(me.filterResults, me);
                    }
                    me.notifyLoadedData(modules);

                    return modules;
                });
        }

        private filterResults(module: Application): boolean {
            if (!this.filterObject) {
                return true;
            }

            var result = true;
            for (var name in this.filterObject) {
                if (this.filterObject.hasOwnProperty(name)) {
                    if (!module.hasOwnProperty(name) || this.filterObject[name] != module[name]) {
                        result = false;
                    }
                }
            }

            return result;
        }

    }
}