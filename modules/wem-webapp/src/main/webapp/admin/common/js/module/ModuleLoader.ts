module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<api.module.ModuleListResult, api.module.ModuleSummary> {

        private delayedFunctionCall: api.util.loader.DelayedFunctionCall;

        private preservedSearchString: string;

        constructor(delay: number = 500) {
            this.loading(false);
            super(new ListModuleRequest(), false);
            this.delayedFunctionCall = new api.util.loader.DelayedFunctionCall(this.load, this, delay);
        }

        search(searchString: string) {

            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.delayedFunctionCall.delayCall();
        }

        load() {
            this.loading(true)
            this.notifyLoadingData();

            this.doRequest()
                .done((modules: api.module.ModuleSummary[]) => {

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