module api.locale {

    export class LocaleLoader extends api.util.loader.BaseLoader<api.locale.json.LocaleListJson, Locale> {

        private preservedSearchString: string;
        protected request: GetLocalesRequest;

        protected createRequest(): GetLocalesRequest {
            return new GetLocalesRequest();
        }

        protected getRequest(): GetLocalesRequest {
            return this.request;
        }

        search(searchString: string): wemQ.Promise<Locale[]> {
            
            this.getRequest().setSearchQuery(searchString);

            return this.load();
        }

        load(): wemQ.Promise<Locale[]> {

            this.notifyLoadingData();

            return this.sendRequest()
                .then((locales: api.locale.Locale[]) => {

                    this.notifyLoadedData(locales);
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }
                    return locales;
                });
        }

    }

}