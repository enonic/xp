module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<ModuleListResult, Module> {

        private preservedSearchString: string;

        constructor(delay: number = 500) {
            super(new ListModulesRequest());
        }

        search(searchString: string) {

            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.load();
        }

        load() {
            this.loading(true);
            this.notifyLoadingData();

            this.sendRequest()
                .done((modules: Module[]) => {

                    this.loading(false);
                    this.notifyLoadedData(modules);
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }
                });
            return null;
        }

    }
}