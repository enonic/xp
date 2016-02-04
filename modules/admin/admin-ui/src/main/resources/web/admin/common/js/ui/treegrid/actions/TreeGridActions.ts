module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import BrowseItem = api.app.browse.BrowseItem;
    import BrowseItemsChanges = api.app.browse.BrowseItemsChanges;

    export interface TreeGridActions<M extends api.Equitable> {

        getAllActions(): Action[];

        updateActionsEnabledState(browseItems: BrowseItem<M>[], changes?: BrowseItemsChanges<any>): wemQ.Promise<BrowseItem<M>[]>;

    }
}
