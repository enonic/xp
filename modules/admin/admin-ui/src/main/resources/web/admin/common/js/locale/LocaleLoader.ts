module api.locale {

    export class LocaleLoader extends api.util.loader.BaseLoader<api.locale.json.LocaleListJson, Locale> {

        private preservedSearchString: string;

        private getLocalesRequest: GetLocalesRequest;

        constructor() {
            this.getLocalesRequest = new GetLocalesRequest();
            super(this.getLocalesRequest);
        }

        search(searchString: string): wemQ.Promise<Locale[]> {
            
            this.getLocalesRequest.setSearchQuery(searchString);

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