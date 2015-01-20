module api.locale {

    export class LocaleLoader extends api.util.loader.BaseLoader<api.locale.json.LocaleListJson, api.locale.Locale> {

        private preservedSearchString: string;

        private getLocalesRequest: GetLocalesRequest;

        constructor(delay: number = 500) {
            this.getLocalesRequest = new GetLocalesRequest();
            super(this.getLocalesRequest);
        }

        search(searchString: string) {
            if (this.isLoading()) {
                this.preservedSearchString = searchString;
                return;
            }

            this.getLocalesRequest.setSearchQuery(searchString);

            this.load();
        }

        load(): void {
            this.notifyLoadingData();

            this.sendRequest()
                .done((locales: api.locale.Locale[]) => {

                    this.notifyLoadedData(locales);
                    if (this.preservedSearchString) {
                        this.search(this.preservedSearchString);
                        this.preservedSearchString = null;
                    }
                });
        }

    }

}