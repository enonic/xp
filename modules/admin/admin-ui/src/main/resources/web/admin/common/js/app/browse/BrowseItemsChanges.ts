module api.app.browse {

    export class BrowseItemsChanges<M extends api.Equitable> {

        private added: BrowseItem<M>[];
        private removed: BrowseItem<M>[];

        constructor(added?: BrowseItem<M>[], removed?: BrowseItem<M>[]) {
            this.added = added || [];
            this.removed = removed || [];
        }

        setAdded(added: BrowseItem<M>[]) {
            this.added = added;
        }

        getAdded(): BrowseItem<M>[] {
            return this.added;
        }

        setRemoved(removed: BrowseItem<M>[]) {
            this.removed = removed;
        }

        getRemoved(): BrowseItem<M>[] {
            return this.removed;
        }
    }

}
