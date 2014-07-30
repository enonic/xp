module app.browse {
    
    import Action = api.ui.Action;

    export class DeleteContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                new ContentDeletePromptEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }
}
