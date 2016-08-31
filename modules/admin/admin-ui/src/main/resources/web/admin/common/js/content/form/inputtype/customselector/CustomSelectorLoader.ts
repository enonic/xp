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

        protected sendPreLoadRequest(ids: string): Q.Promise<CustomSelectorItem[]> {

            return this.customSelectorRequest.setIds(ids.split(";")).sendAndParse().then((results) => {
                this.customSelectorRequest.setIds([]);
                return results;
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