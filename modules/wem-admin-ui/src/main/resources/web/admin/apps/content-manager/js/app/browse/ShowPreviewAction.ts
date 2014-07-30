module app.browse {
    
    import Action = api.ui.Action;

    export class ShowPreviewAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("PREVIEW");

            this.setEnabled(false);
            this.onExecuted(() => {
                new ShowPreviewEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }
}
