module api.ui.toolbar {

    export class Toolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private fold: Fold;

        private hasGreedySpacer: boolean;

        private actions:api.ui.Action[] = [];

        constructor() {
            super("toolbar");

            this.fold = new Fold();
            this.fold.addClass("pull-right").hide();
            this.appendChild(this.fold);

            window.addEventListener("resize", () => this.foldOrExpand());

            this.onRendered((event) => {
                this.foldOrExpand();
            })
        }

        addAction(action:api.ui.Action) {
            this.actions.push(action);
            this.addElement(new api.ui.ActionButton(action));
        }

        getActions():api.ui.Action[] {
            return this.actions;
        }

        addElement(element:api.dom.Element) {
            if (this.hasGreedySpacer) {
                element.addClass('pull-right');
                element.insertAfterEl(this.fold);
            } else {
                element.insertBeforeEl(this.fold);
            }
        }

        addGreedySpacer() {
            this.hasGreedySpacer = true;
        }

        private foldOrExpand() {
            if (!this.isVisible()) {
                return;
            }

            var toolbarWidth = this.getEl().getWidth();
            if (toolbarWidth <= this.getDisplayedButtonsWidth()) {
                do {
                    var buttonToHide = this.getLastRightButton() || this.getLastLeftButton();
                    if (!buttonToHide) {
                        return;
                    }
                    this.removeChild(buttonToHide);
                    this.fold.push(buttonToHide);
                    this.fold.show();
                } while (toolbarWidth <= this.getDisplayedButtonsWidth());
            } else if (!this.fold.isEmpty()) {
                do {
                    var buttonToShow = this.fold.pop();
                    buttonToShow.getEl().setVisibility('hidden');
                    buttonToShow.hasClass('pull-right') ? buttonToShow.insertAfterEl(this.fold) : buttonToShow.insertBeforeEl(this.fold);
                    if (toolbarWidth <= this.getDisplayedButtonsWidth()) {
                        this.removeChild(buttonToShow);
                        this.fold.push(buttonToShow);
                        buttonToShow.getEl().setVisibility('visible');
                        return;
                    }
                    buttonToShow.getEl().setVisibility('visible');
                } while (!this.fold.isEmpty());
                this.fold.hide();
            }
        }

        private getDisplayedButtonsWidth(): number {
            return this.getChildren().reduce((totalWidth: number, element: api.dom.Element) => {
                return totalWidth + element.getEl().getMarginLeft() + element.getEl().getWidth() + element.getEl().getMarginRight();
            }, 0);
        }

        private getLastLeftButton(): api.dom.Element {
            return this.getChildren()[this.getChildren().indexOf(this.fold) - 1];
        }

        private getLastRightButton(): api.dom.Element {
            return this.getChildren()[this.getChildren().indexOf(this.fold) + 1];
        }

    }

}
