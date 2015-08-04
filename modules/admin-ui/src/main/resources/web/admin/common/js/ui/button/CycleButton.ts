module api.ui.button {


    export class CycleButton extends api.dom.DivEl {

        private actionList: Action[];

        private active: number;

        constructor(actions: Action[], label: string = "") {
            super("button cycle-button");
            this.actionList = actions;

            this.setHtml(label);

            if (this.actionList.length > 0) {
                this.active = -1;
                this.updateActive();

                this.onClicked(() => {
                    this.doAction();
                });
            }
        }

        private doAction() {
            this.actionList[this.active].execute();
            this.updateActive();
        }

        private updateActive() {
            var name, prevName;

            prevName = this.actionList[this.active] ? this.actionList[this.active].getLabel().toLowerCase() : "";

            this.active++;

            if (this.active >= this.actionList.length) {
                this.active = 0;
            }

            name = this.actionList[this.active] ? this.actionList[this.active].getLabel().toLowerCase() : "";

            if (prevName) {
                this.removeClass(prevName);
            }
            if (name) {
                this.addClass(name);
            }
        }

        executePrevAction() {
            var prev = this.active - 1;
            prev = prev < 0 ? this.actionList.length - 1 : prev;

            if (this.actionList.length > 0) {
                this.actionList[prev].execute();
            }
        }

    }
}