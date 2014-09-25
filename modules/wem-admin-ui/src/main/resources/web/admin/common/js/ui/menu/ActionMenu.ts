module api.ui.menu {


    export class ActionMenu extends api.dom.DivEl {

        private actionList: Action[];
        private actionListEl: api.dom.UlEl;
        private labelEl: api.dom.DivEl;

        constructor(label: string, ...actions: Action[]) {
            super("action-menu");
            this.labelEl = new api.dom.DivEl("drop-down-button icon-arrow-down");
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);

            this.actionListEl = new api.dom.UlEl();
            this.appendChild(this.actionListEl);

            this.actionList = actions;


            this.actionList.forEach((action: Action, i: number) => {
                var el = this.addAction(action, i);
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

        private addAction(action: Action, index: number): api.dom.DivEl {
            var actionEl = new ActionMenuItem(action, index);

            actionEl.onClicked(() => {
                this.removeClass("expanded");
                if (actionEl.hasClass("active")) {
                    this.doAction(this.nextActionIndex(index));
                } else {
                    this.doAction(index);
                }

            });
            this.actionListEl.appendChild(actionEl);
            return actionEl
        }

        private nextActionIndex(index: number): number {
            index = index + 1;
            if (index < this.actionList.length) {
                if (this.actionList[index].isEnabled()) {
                    return index;
                } else {
                    return this.nextActionIndex(index);
                }
            } else {
                return 0;
            }
        }

        private doAction(index: number) {
            while (!this.actionList[index].isEnabled()) {
                index++;
            }
            this.actionList[index].execute();
        }
    }
}