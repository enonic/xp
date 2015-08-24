module app.browse {

    import FoldButton = api.ui.toolbar.FoldButton;

    export class MobileContentBrowseToolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private fold: FoldButton;

        private actions: api.ui.Action[] = [];

        constructor(actions: app.browse.action.MobileContentTreeGridActions) {
            super("toolbar");

            this.initFoldButton();

            this.addActions(actions.getAllActions());

            this.initEditButton(actions.EDIT_CONTENT);
        }

        private initFoldButton() {
            this.fold = new FoldButton();
            this.fold.setLabel('More...');
            this.appendChild(this.fold);
        }

        private initEditButton(editAction: api.ui.Action) {
            var editButton = new api.ui.button.ActionButton(editAction);
            editButton.addClass("mobile-edit-action");
            this.appendChild(editButton);
        }

        addActions(actions: api.ui.Action[]) {
            this.actions = this.actions.concat(actions);
            actions.forEach((action) => {

                var actionButton = new api.ui.button.ActionButton(action);
                var buttonWidth = actionButton.getEl().getWidthWithBorder();
                this.fold.push(actionButton, buttonWidth);
            });
        }

        getActions(): api.ui.Action[] {
            return this.actions;
        }

    }
}
