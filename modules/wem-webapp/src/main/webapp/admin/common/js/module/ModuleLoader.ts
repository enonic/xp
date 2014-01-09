module api.module {

    export class ModuleLoader implements api.util.Loader
    {
        private findModuleRequest:ListModuleRequest;

        private loaderHelper:api.util.LoaderHelper;

        private isLoading:boolean;

        private preservedSearchString:string;


        private listeners:ModuleLoaderListener[] = [];

        constructor(delay:number = 500) {
            this.isLoading = false;
            this.findModuleRequest = new ListModuleRequest();
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

            this.findModuleRequest.sendAndParse()
                .done((modules:api.module.ModuleSummary[]) => {

                          this.isLoading = false;
                          this.notifyLoaded(modules);
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