module api.ui.toolbar {

    export class Toolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private fold: FoldButton;

        private hasGreedySpacer: boolean;

        private actions: api.ui.Action[] = [];

        constructor() {
            super("toolbar");

            this.fold = new FoldButton();
            this.fold.hide();
            this.appendChild(this.fold);

            api.dom.WindowDOM.get().onResized((event: UIEvent) => this.foldOrExpand(), this);

            this.onShown((event) => this.foldOrExpand());
        }

        addAction(action: api.ui.Action) {
            this.actions.push(action);
            this.addElement(new api.ui.button.ActionButton(action));
        }

        addActions(actions: api.ui.Action[]) {
            this.actions = this.actions.concat(actions);
            actions.forEach((action) => {
                this.addElement(new api.ui.button.ActionButton(action));
            });
        }

        removeActions() {
            this.actions.forEach((action: api.ui.Action) => {
                this.getChildren().forEach((element: api.dom.Element) => {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(element, api.ui.button.ActionButton)) {
                        if (action.getLabel() == (<api.ui.button.ActionButton>element).getLabel()) {
                            this.removeChild(element);
                        }
                    }
                });
            });
            this.actions = [];
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

                while (toolbarWidth <= this.getVisibleButtonsWidth() && this.getNextFoldableButton()) {

                    var buttonToHide = this.getNextFoldableButton();
                    var buttonWidth = buttonToHide.getEl().getWidthWithBorder();

                    this.removeChild(buttonToHide);
                    this.fold.push(buttonToHide, buttonWidth);

                    if (!this.fold.isVisible()) {
                        this.fold.show();
                    }
                }

            } else {
                // if fold has 1 child left then subtract fold button width because it will be hidden
                while (!this.fold.isEmpty() &&
                       (this.getVisibleButtonsWidth(this.fold.getButtonsCount() > 1) + this.fold.getNextButtonWidth() < toolbarWidth)) {

                    var buttonToShow = this.fold.pop();
                    buttonToShow.insertBeforeEl(this.fold);

                    if (this.fold.isEmpty()) {
                        this.fold.hide();
                    }
                }
            }

            this.fold.setLabel(this.getFoldableButtons().length == 0 ? 'Actions' : 'More');
        }

        private getVisibleButtonsWidth(includeFold: boolean = true): number {
            return this.getChildren().reduce((totalWidth: number, element: api.dom.Element) => {
                return totalWidth + ( element.isVisible() && (includeFold || element != this.fold) ?
                                      element.getEl().getWidthWithMargin() : 0 );
            }, 0);
        }

        private getNextFoldableButton(): api.dom.Element {
            return this.getChildren()[this.getChildren().indexOf(this.fold) - 1];
        }

        private getFoldableButtons(): api.dom.Element[] {
            return this.getChildren().slice(0, this.getChildren().indexOf(this.fold));
        }

    }

}
