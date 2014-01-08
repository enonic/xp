module api.module {

    export class ModuleLoader implements api.util.Loader
    {
        private findModuleRequest:ModuleResourceRequest<ModuleListResult>;

        private loaderHelper:api.util.LoaderHelper;

        private isLoading:boolean;

        private preservedSearchString:string;


        private listeners:ModuleLoaderListener[] = [];

        constructor(delay:number = 500, findModuleRequest:ModuleResourceRequest<ModuleListResult> = new ListModuleRequest()) {
            this.isLoading = false;
            this.findModuleRequest = findModuleRequest;
            this.loaderHelper = new api.util.LoaderHelper(this.doRequest, this, delay);
        }

        search(searchString:string) {
            if (this.isLoading) {
                this.preservedSearchString = searchString;
                return;
            }

            this.loaderHelper.search(searchString);
        }

        private doRequest(searchString:string) {
            this.isLoading = true;
            this.notifyLoading();

            this.findModuleRequest.send()
                .done((jsonResponse:api.rest.JsonResponse<api.module.ModuleListResult>) => {
                          var result = jsonResponse.getResult();

                          this.isLoading = false;
                          this.notifyLoaded(api.module.ModuleSummary.fromJsonArray(result.modules));
                          if (this.preservedSearchString) {
                              this.search(this.preservedSearchString);
                              this.preservedSearchString = null;
                          }
                      });
        }

        addListener(listener:ModuleLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove:ModuleLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener:ModuleLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(modules:api.module.ModuleSummary[]) {
            this.listeners.forEach((listener:ModuleLoaderListener) => {
                if (listener.onLoaded) {
                    console.log("notifiyng", modules);
                    listener.onLoaded(modules);
                }
            });
        }
    }
}