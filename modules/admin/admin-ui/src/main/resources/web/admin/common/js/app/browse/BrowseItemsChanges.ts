module api.app.browse {

    export class BrowseItemsChanges {

        private added: BrowseItem<any>[];
        private removed: BrowseItem<any>[];

        constructor(added?: BrowseItem<any>[], removed?: BrowseItem<any>[]) {
            this.added = added || [];
            this.removed = removed || [];
        }

        setAdded(added: BrowseItem<any>[]) {
            this.added = added;
        }

        getAdded(): BrowseItem<any>[] {
            return this.added;
        }

        setRemoved(removed: BrowseItem<any>[]) {
            this.removed = removed;
        }

        getRemoved(): BrowseItem<any>[] {
            return this.removed;
        }
    }

}
