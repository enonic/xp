module app.browse {
    
    import Action = api.ui.Action;

    export class OpenContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Open", "mod+o");
            this.setEnabled(false);
            this.onExecuted(() => {
                new ViewContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }
}
