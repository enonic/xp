module app.browse {
    
    import Action = api.ui.Action;

    export class DuplicateContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                new DuplicateContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }

}
