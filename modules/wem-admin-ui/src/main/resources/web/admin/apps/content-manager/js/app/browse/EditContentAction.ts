module app.browse {
    
    import Action = api.ui.Action;

    export class EditContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.onExecuted(() => {
                var content = this.extModelsToContentSummaries(treeGridPanel.getSelection());
                new EditContentEvent(content).fire();
            });
        }
    }
}
