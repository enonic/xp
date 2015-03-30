module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export interface TreeGridActions<M extends api.Equitable> {

        getAllActions(): Action[];

        updateActionsEnabledState(browseItems: api.app.browse.BrowseItem<M>[]): wemQ.Promise<api.app.browse.BrowseItem<M>[]>;

    }
}
