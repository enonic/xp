module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<ModuleListResult, Module> {

        private preservedSearchString: string;

        constructor(delay: number = 500) {
            super(new ListModulesRequest());
        }

        search(searchString: string): wemQ.Promise<Module[]> {

            return this.load();
        }

        load(): wemQ.Promise<Module[]> {

            this.notifyLoadingData();

            return this.sendRequest()
                .then((modules: Module[]) => {

                    this.notifyLoadedData(modules);
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }
                    return modules;
                });
        }

    }
}