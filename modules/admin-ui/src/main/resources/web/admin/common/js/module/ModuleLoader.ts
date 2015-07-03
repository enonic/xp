module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<ModuleListResult, Module> {

        private listModulesRequest: ListModulesRequest;

        private filterObject: Object;

        constructor(delay: number = 500, filterObject: Object = null) {
            super(this.listModulesRequest = new ListModulesRequest());
            if (filterObject) {
                this.filterObject = filterObject;
            }
        }

        search(searchString: string): wemQ.Promise<Module[]> {
            this.listModulesRequest.setSearchQuery(searchString);
            return this.load();
        }

        load(): wemQ.Promise<Module[]> {
            var me = this;
            me.notifyLoadingData();

            return me.sendRequest()
                .then((modules: Module[]) => {
                    if (me.filterObject) {
                        modules = modules.filter(me.filterResults, me);
                    }
                    me.notifyLoadedData(modules);

                    return modules;
                });
        }

        private filterResults(module: Module): boolean {
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