module api.app.wizard {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import Toolbar = api.ui.toolbar.Toolbar;


    export class WizardStepNavigatorAndToolbar extends api.dom.DivEl {

        private foldButton: api.ui.toolbar.FoldButton;

        private stepToolbar: Toolbar;

        private stepNavigator: WizardStepNavigator;

        private minimized: boolean;

        private maxFittingWidth: number;

        private helpTextToggleButton: api.dom.DivEl;

        constructor(className?: string) {
            super(className);
            this.foldButton = new api.ui.toolbar.FoldButton();
            this.foldButton.setLabel("Navigate to");
            this.appendChild(this.foldButton);
            this.minimized = false;
            this.maxFittingWidth = 0;

            this.foldButton.getDropdown().onClicked(() => {
                this.addClass("no-dropdown-hover");
                // Place call in the queue outside of the stack and current context,
                // so the repaint will be triggered between those two calls
                setTimeout(this.removeClass.bind(this, "no-dropdown-hover"));
            });
        }

        setupHelpTextToggleButton(): api.dom.DivEl {
            this.helpTextToggleButton = new api.dom.DivEl("help-text-button");

            this.appendChild(this.helpTextToggleButton);
            this.checkAndMinimize();

            return this.helpTextToggleButton;
        }


        setStepToolbar(stepToolbar: Toolbar) {
            if (this.stepToolbar) {
                this.removeChild(this.stepToolbar);
            }
            this.stepToolbar = stepToolbar;

            this.stepToolbar.insertBeforeEl(this.foldButton);
        }

        setStepNavigator(stepNavigator: WizardStepNavigator) {
            if (this.stepNavigator) {
                this.removeChild(this.stepNavigator);
            }
            this.stepNavigator = stepNavigator;

            this.appendChild(this.stepNavigator);
        }

        private isStepNavigatorFit(): boolean {
            let helpToggleBtnWidth = this.helpTextToggleButton ? this.helpTextToggleButton.getEl().getWidth() : 0;
            let width = this.helpTextToggleButton
                ? this.stepNavigator.getEl().getMaxWidth() - helpToggleBtnWidth
                : this.getEl().getWidthWithoutPadding();
            if (this.stepNavigator.isVisible()) {
                this.maxFittingWidth = this.stepNavigator.getChildren().reduce((prevWidth, child) => {
                    return prevWidth + child.getEl().getWidthWithMargin();
                }, 0);
            }

            return this.maxFittingWidth < width;
        }

        checkAndMinimize() {
            if (this.isStepNavigatorFit() == this.minimized) {
                this.minimized = !this.minimized;
                this.toggleClass('minimized', this.minimized);

                if (this.minimized) {
                    this.removeChild(this.stepNavigator);
                    this.foldButton.push(this.stepNavigator, 300);
                } else {
                    this.foldButton.pop();
                    this.stepNavigator.insertAfterEl(this.foldButton);
                }
            }
        }
    }
}
