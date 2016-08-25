module api.content.form.inputtype.contentselector {

    import PostLoader = api.util.loader.PostLoader;

    export class CustomSelectorLoader extends PostLoader<CustomSelectorResponse, CustomSelectorItem> {

        private customSelectorRequest: CustomSelectorRequest;

        constructor(requestPath: string) {
            this.customSelectorRequest = new CustomSelectorRequest(requestPath);
            super(this.customSelectorRequest);
        }

        search(searchString: string): wemQ.Promise<CustomSelectorItem[]> {

            this.customSelectorRequest.setQuery(searchString);
            return this.load();
        }

        preLoad(ids: string): wemQ.Promise<CustomSelectorItem[]> {
            this.notifyLoadingData(false);

            return this.customSelectorRequest.setIds(ids.split(";")).sendAndParse().then((results: CustomSelectorItem[]) => {
                // reset ids
                this.customSelectorRequest.setIds(null);

                if (this.getComparator()) {
                    this.setResults(results.sort(this.getComparator().compare));
                } else {
                    this.setResults(results);
                }
                this.notifyLoadedData(results);
                return this.getResults();
            });
        }
        

        resetParams() {
            return this.customSelectorRequest.resetParams();
        }

        isPartiallyLoaded(): boolean {
            return this.customSelectorRequest.isPartiallyLoaded();
        }

        filterFn(item: CustomSelectorItem): boolean {
            return item.displayName.indexOf(this.getSearchString().toLowerCase()) != -1;
        }
    }

}