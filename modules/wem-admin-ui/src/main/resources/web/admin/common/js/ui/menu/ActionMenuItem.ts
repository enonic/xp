module api.ui.menu {

    export class ActionMenuItem extends api.dom.LiEl {

        private action: api.ui.Action;

        constructor(action: api.ui.Action, index: number) {

            super("action");
            this.action = action;

            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().setData("action", index + "");

            this.action.onPropertyChanged(() => {
                if (this.action.isEnabled() && !this.isVisible()) {
                    this.show();
                }
                else if (!this.action.isEnabled() && this.isVisible()) {
                    this.hide();
                }
            });
        }
    }
}