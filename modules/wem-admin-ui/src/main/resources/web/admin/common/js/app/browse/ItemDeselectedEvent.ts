module api.app.browse {

    export class ItemDeselectedEvent<M extends api.Equitable> {

        private browseItem: BrowseItem<M>;

        constructor(browseItem: BrowseItem<M>) {
            this.browseItem = browseItem;
        }

        getBrowseItem(): BrowseItem<M> {
            return this.browseItem;
        }
    }
}