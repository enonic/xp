module api.content.form.inputtype.customselector {

    import PostLoader = api.util.loader.PostLoader;

    export class CustomSelectorLoader extends PostLoader<CustomSelectorResponse, CustomSelectorItem> {

        protected request: CustomSelectorRequest;

        constructor(requestPath: string) {
            super();

            this.getRequest().setRequestPath(requestPath);
        }

        protected createRequest(): CustomSelectorRequest {
            return new CustomSelectorRequest();
        }

        protected getRequest(): CustomSelectorRequest {
            return this.request;
        }

        search(searchString: string): wemQ.Promise<CustomSelectorItem[]> {

            this.getRequest().setQuery(searchString);
            return this.load();
        }

        protected sendPreLoadRequest(ids: string): Q.Promise<CustomSelectorItem[]> {

            return this.getRequest().setIds(ids.split(";")).sendAndParse().then((results) => {
                this.getRequest().setIds([]);
                return results;
            });
        }

        resetParams() {
            return this.getRequest().resetParams();
        }

        isPartiallyLoaded(): boolean {
            return this.getRequest().isPartiallyLoaded();
        }

        filterFn(item: CustomSelectorItem): boolean {
            return item.displayName.indexOf(this.getSearchString().toLowerCase()) != -1;
        }
    }

}