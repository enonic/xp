module app.browse {
    
    import Action = api.ui.Action;

    export class ShowDetailsAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("DETAILS");

            this.setEnabled(true);
            this.onExecuted(() => {
                new ShowDetailsEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            })
        }
    }
}
