module api.ui.button {


    export class CycleButton extends api.ui.button.Button {

        private actionList: Action[];

        private active: number;

        constructor(actions: Action[]) {
            super();
            this.addClass("cycle-button icon-screen icon-medium");
            this.actionList = actions;

            if (this.actionList.length > 0) {
                this.active = -1;
                this.updateActive();
                this.setTitle(this.actionList[this.active].getTitle(), false);

                this.onClicked(() => {
                    this.removeAndHdeTitle();
                    this.doAction();
                    this.setAndShowTitle();
                });
            }
        }

        private doAction() {
            this.actionList[this.active].execute();
            this.updateActive();
        }

        private removeAndHdeTitle() {
            if (this.actionList[this.active].getTitle()) {
                this.setTitle("");
            }
        }

        private setAndShowTitle() {
            var title = this.actionList[this.active].getTitle();
            if (title) {
                this.setTitle(title);
            }
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
                    this.setTitle(this.actionList[this.active].getTitle(), false);

                    return;
                }
            }
            console.warn('Action not found in CycleButton', action);
        }

    }
}