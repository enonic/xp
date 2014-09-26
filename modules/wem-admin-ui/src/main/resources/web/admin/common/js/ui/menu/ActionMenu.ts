module api.ui.menu {

    export class ActionMenu extends api.dom.DivEl {

        private actionListEl: api.dom.UlEl;

        private labelEl: api.dom.DivEl;

        constructor(label: string, ...actions: Action[]) {
            super("action-menu");
            this.labelEl = new api.dom.DivEl("drop-down-button icon-arrow-down");
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);

            this.actionListEl = new api.dom.UlEl();
            this.appendChild(this.actionListEl);

            actions.forEach((action: Action) => {
                this.addAction(action);
            });

            this.labelEl.onClicked((event) => {
                if (this.hasClass("expanded")) {
                    this.removeClass("expanded");
                } else {
                    this.addClass("expanded");
                }
            });
        }

        setLabel(label: string) {
            this.labelEl.getEl().setInnerHtml(label);
        }

        private addAction(action: Action) {
            var actionMenuItem = new ActionMenuItem(action);
            this.actionListEl.appendChild(actionMenuItem);
            actionMenuItem.onClicked(() => {
                this.removeClass("expanded");
            });
        }
    }
}