module api.module {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export class ModuleLoader extends api.util.loader.BaseLoader<api.module.ModuleListResult, api.module.ModuleSummary>
    {
        private loaderHelper:api.util.loader.LoaderHelper;

        private preservedSearchString:string;

        constructor(delay:number = 500) {
            this.loading(false);
            super(new ListModuleRequest(), false);
            this.loaderHelper = new api.util.loader.LoaderHelper(this.load, this, delay);
        }

        search(searchString:string) {
            if (this.loading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.loaderHelper.search(searchString);
        }

        load() {
            this.loading(true)
            this.notifyLoadingData(new LoadingDataEvent());

            this.doRequest()
                .done((modules:api.module.ModuleSummary[]) => {

                          this.loading(false);
                          this.notifyLoadedData(new LoadedDataEvent<api.module.ModuleSummary>(modules));
                          if (this.preservedSearchString) {
                              this.search(this.preservedSearchString);
                              this.preservedSearchString = null;
                          }
                      });
            return null;
        }

    }
}