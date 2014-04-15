module api.ui.toolbar {

    export class Toolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private fold: FoldButton;

        private hasGreedySpacer: boolean;

        private actions: api.ui.Action[] = [];

        constructor() {
            super("toolbar");

            this.fold = new FoldButton();
            this.appendChild(this.fold);

            api.dom.Window.get().onResized((event: UIEvent) => this.foldOrExpand(), this);

            this.onShown((event) => this.foldOrExpand());
        }

        addAction(action: api.ui.Action) {
            this.actions.push(action);
            this.addElement(new api.ui.ActionButton(action));
        }

        getActions(): api.ui.Action[] {
            return this.actions;
        }

        addElement(element: api.dom.Element) {
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
            if (toolbarWidth < this.getVisibleButtonsWidth()) {

                do {
                    var buttonToHide = this.getLastLeftButton();
                    if (!buttonToHide) {
                        return;
                    }
                    var buttonWidth = buttonToHide.getEl().getWidthWithBorder();
                    this.removeChild(buttonToHide);
                    this.fold.push(buttonToHide, buttonWidth);
                    console.log('Folding button', buttonToHide);

                    if (!this.fold.isVisible()) {
                        this.fold.show();
                    }
                } while (toolbarWidth <= this.getVisibleButtonsWidth());

            } else {

                while (!this.fold.isEmpty() && (this.getVisibleButtonsWidth() + this.fold.getNextButtonWidth() < toolbarWidth)) {

                    var buttonToShow = this.fold.pop();
                    buttonToShow.insertBeforeEl(this.fold);
                    console.log('Unfolding button', buttonToShow);

                    if (this.fold.isEmpty()) {
                        this.fold.hide();
                    }
                }
            }
        }

        private getVisibleButtonsWidth(): number {
            return this.getChildren().reduce((totalWidth: number, element: api.dom.Element) => {
                return totalWidth + ( element.isVisible() ? element.getEl().getWidthWithMargin() : 0 );
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
