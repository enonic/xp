module api.ui.button {


    export class CycleButton extends api.dom.DivEl {

        private actionList: Action[];

        constructor(...actions: Action[]) {
            super("button cycle-button");
            this.actionList = actions;

            this.actionList.forEach((action: Action, i: number) => {
                var el = this.addAction(action, i);
                if (i == 0) {
                    this.setActive(el);
                }
            });

            var dropDownButton = new api.dom.DivEl("drop-down-button");
            dropDownButton.onClicked((event) => {
                if (this.hasClass("expanded")) {
                    this.removeClass("expanded");
                } else {
                    this.addClass("expanded");
                }
            });
            this.appendChild(dropDownButton);
        }

        disableAction(action: Action) {
            if (action.isEnabled()) {
                action.setEnabled(false);
                this.getElementFromIndex(this.getIndexFromAction(action)).addClass("hidden");
            }
        }

        enableAction(action: Action) {
            action.setEnabled(true);
            this.getElementFromIndex(this.getIndexFromAction(action)).removeClass("hidden");
        }

        setCurrentAction(action: Action) {
            this.doAction(this.getIndexFromAction(action));
        }

        private addAction(action: Action, index: number): api.dom.DivEl {
            var cycleActionEl = new api.dom.DivEl("action");
            cycleActionEl.getEl().setInnerHtml(action.getLabel());
            cycleActionEl.getEl().setData("action", index + "");

            cycleActionEl.onClicked(() => {
                this.removeClass("expanded");
                if (cycleActionEl.hasClass("active")) {
                    this.doAction(this.nextActionIndex(index));
                } else {
                    this.doAction(index);
                }

            });
            this.appendChild(cycleActionEl);
            return cycleActionEl
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
            this.setActive(this.getElementFromIndex(index));
            this.actionList[index].execute();
        }

        private getIndexFromAction(action: Action) {
            var index = -1;
            this.actionList.forEach((a, i) => {
                if (a == action) {
                    index = i;
                }
            });
            return index;
        }

        private getElementFromIndex(index: number): api.dom.Element {
            var element = null;
            this.getChildren().forEach((actionEl) => {
                if (parseInt(actionEl.getEl().getData("action")) == index) {
                    element = actionEl;
                }
            });
            return element;
        }

        private setActive(listItem: api.dom.Element) {
            this.getChildren().forEach((child) => {
                child.removeClass("active");
            });
            listItem.addClass("active");
            this.removeChild(listItem);
            this.prependChild(listItem);
        }


    }
}