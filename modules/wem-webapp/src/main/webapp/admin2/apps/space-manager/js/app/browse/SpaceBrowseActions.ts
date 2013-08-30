module app_browse {

    export class NewSpaceAction extends api_ui.Action {

        constructor() {
            super("New");
            this.addExecutionListener(() => {
                new NewSpaceEvent().fire();
            });
        }
    }

    export class OpenSpaceAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Open");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new OpenSpaceEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class EditSpaceAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Edit");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new EditSpaceEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class DeleteSpaceAction extends api_ui.Action {

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                new app_browse.SpaceDeletePromptEvent(treeGridPanel.getSelection()).fire();
            });
        }
    }

    export class SpaceBrowseActions {

        public NEW_SPACE:api_ui.Action;
        public OPEN_SPACE:api_ui.Action;
        public EDIT_SPACE:api_ui.Action;
        public DELETE_SPACE:api_ui.Action;

        private allActions:api_ui.Action[] = [];

        private static INSTANCE:SpaceBrowseActions;

        static init(treeGridPanel:api_app_browse_grid.TreeGridPanel):SpaceBrowseActions {
            new SpaceBrowseActions(treeGridPanel);
            return SpaceBrowseActions.INSTANCE;
        }

        static get():SpaceBrowseActions {
            return SpaceBrowseActions.INSTANCE;
        }

        constructor(treeGridPanel:api_app_browse_grid.TreeGridPanel) {

            this.NEW_SPACE = new NewSpaceAction();
            this.OPEN_SPACE = new OpenSpaceAction(treeGridPanel);
            this.EDIT_SPACE = new EditSpaceAction(treeGridPanel);
            this.DELETE_SPACE = new DeleteSpaceAction(treeGridPanel);


            this.allActions.push(this.NEW_SPACE, this.OPEN_SPACE, this.EDIT_SPACE, this.DELETE_SPACE);

            SpaceBrowseActions.INSTANCE = this;
        }

        updateActionsEnabledState(models:api_model.SpaceExtModel[]) {

            if (models.length <= 0) {
                this.NEW_SPACE.setEnabled(true);
                this.OPEN_SPACE.setEnabled(false);
                this.EDIT_SPACE.setEnabled(false);
                this.DELETE_SPACE.setEnabled(false);
            }
            else if (models.length == 1) {
                this.NEW_SPACE.setEnabled(false);
                this.OPEN_SPACE.setEnabled(true);
                this.EDIT_SPACE.setEnabled(models[0].data.editable);
                this.DELETE_SPACE.setEnabled(models[0].data.deletable);
            }
            else {
                this.NEW_SPACE.setEnabled(false);
                this.OPEN_SPACE.setEnabled(true);
                this.EDIT_SPACE.setEnabled(this.anyEditable(models));
                this.DELETE_SPACE.setEnabled(this.anyDeleteable(models));
            }
        }

        getAllActions():api_ui.Action[] {
            return this.allActions;
        }

        private anyEditable(spaces:api_model.SpaceExtModel[]):boolean {
            for (var i in spaces) {
                var space:api_model.SpaceExtModel = spaces[i];
                if (space.data.editable) {
                    return true;
                }
            }
            return false;
        }

        private anyDeleteable(spaces:api_model.SpaceExtModel[]):boolean {
            for (var i in spaces) {
                var space:api_model.SpaceExtModel = spaces[i];
                if (space.data.deletable) {
                    return true;
                }
            }
            return false;
        }
    }
}
