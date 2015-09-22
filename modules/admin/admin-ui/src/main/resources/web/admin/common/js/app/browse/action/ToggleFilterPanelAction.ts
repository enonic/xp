module api.app.browse.action {

    import Action = api.ui.Action;

    export class ToggleFilterPanelAction extends Action {

        constructor(browsePanel: BrowsePanel<any>) {
            super("");
            this.setIconClass("icon-search")
            this.setEnabled(true);
            this.onExecuted(() => {
                browsePanel.toggleFilterPanel();
            });
        }
    }
}