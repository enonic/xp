module app.browse {
    
    import Action = api.ui.Action;

    export class MoveContentAction extends BaseContentBrowseAction {

        constructor(treeGridPanel: api.app.browse.grid.TreeGridPanel) {
            super("Move");
            this.setEnabled(false);
            this.onExecuted(() => {
                new MoveContentEvent(this.extModelsToContentSummaries(treeGridPanel.getSelection())).fire();
            });
        }
    }
}
