module app.browse {
    
    import Action = api.ui.Action;

    export class ShowNewContentDialogAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("New", "mod+alt+n");
            this.setEnabled(true);
            this.onExecuted(() => {
                var extModelsToContentSummaries: api.content.ContentSummary[] = this.extModelsToContentSummaries(treeGridPanel.getSelection());
                new ShowNewContentDialogEvent(extModelsToContentSummaries.length > 0 ? extModelsToContentSummaries[0] : null).fire();
            });
        }
    }
}
