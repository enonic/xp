module api.ui.button {


    export class CycleButton extends api.ui.button.Button {

        private actionList: Action[];

        private active: number;

        constructor(actions: Action[]) {
            super();
            this.addClass("cycle-button icon-devices icon-medium");
            this.actionList = actions;

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

        selectActiveAction(action: Action) {
            var i, l = this.actionList.length;
            for (i = 0; i < l; i++) {
                if (this.actionList[i] === action) {
                    this.active = i;
                    this.updateActive();
                    return;
                }
            }
            console.warn('Action not found in CycleButton', action);
        }

    }
}