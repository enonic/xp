module api.app.wizard {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import Toolbar = api.ui.toolbar.Toolbar;
    import TabBarItem = api.ui.tab.TabBarItem;
    import ActivatedEvent = api.ui.ActivatedEvent;

    export class WizardStepNavigatorAndToolbar extends api.dom.DivEl {

        static maxFittingWidth: number = 675;

        private foldButton: api.ui.toolbar.FoldButton;

        private stepToolbar: Toolbar;

        private stepNavigator: WizardStepNavigator;

        private fittingWidth: number;

        private helpTextToggleButton: api.dom.DivEl;

        constructor(className?: string) {
            super(className);
            this.foldButton = new api.ui.toolbar.FoldButton();
            this.appendChild(this.foldButton);
            this.fittingWidth = 0;

            this.foldButton.getDropdown().onClicked(() => {
                this.addClass("no-dropdown-hover");
                // Place call in the queue outside of the stack and current context,
                // so the repaint will be triggered between those two calls
                setTimeout(this.removeClass.bind(this, "no-dropdown-hover"));
            });
        }

        setupHelpTextToggleButton(): api.dom.DivEl {
            this.helpTextToggleButton = new api.dom.DivEl("help-text-button");

            this.addClass("has-help-text-button");
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
            let onStepChanged = (event: ActivatedEvent) => {
                this.onStepChanged(event.getIndex());
            };
            if (this.stepNavigator) {
                this.stepNavigator.unNavigationItemActivated(onStepChanged);
                this.removeChild(this.stepNavigator);
            }
            this.stepNavigator = stepNavigator;

            this.appendChild(this.stepNavigator);

            this.stepNavigator.onNavigationItemActivated(onStepChanged);
        }

        private onStepChanged(index: number): void {
            let tabBarItem: TabBarItem = this.stepNavigator.getNavigationItem(index);
            if (tabBarItem) {
                this.foldButton.setLabel(tabBarItem.getLabel());
            }
        }

        private isStepNavigatorFit(): boolean {
            let width;

            if (this.stepNavigator.isVisible()) {
                // StepNavigator fits if summary width of its steps < width of StepNavigator
                // StepNavigator width is calculated in CSS
                width = this.stepNavigator.getEl().getWidthWithoutPadding();
                const steps = this.stepNavigator.getChildren();
                const stepMargin = (step) => step.isVisible() ? step.getEl().getWidthWithMargin() : 0;
                const stepsWidth = steps.reduce((totalStepWidth, step) => totalStepWidth + stepMargin(step), 0);

                // Update fitting width to check, when toolbar is minimized
                this.fittingWidth = stepsWidth;

                return width > stepsWidth;
            } else {
                // StepNavigator is minimized and not visible
                // Check with saved width
                const help = this.helpTextToggleButton;
                width = help.isVisible() ?
                        this.getEl().getWidthWithoutPadding() - help.getEl().getWidthWithMargin() :
                        this.getEl().getWidthWithoutPadding();
            }

            // Add to pixels delta to made the check work as it should, when scale is not 100%
            const fittingWidth = Math.min(this.fittingWidth, WizardStepNavigatorAndToolbar.maxFittingWidth) + 2;

            return width > fittingWidth;
        }

        private updateStepLabels(numberTabs: boolean) {
            let selectedTabIndex = this.stepNavigator.getSelectedIndex();
            this.stepNavigator.getNavigationItems().forEach((tab: api.ui.tab.TabBarItem, index) => {
                let strIndex = (index + 1) + " - ";
                if (numberTabs && tab.getLabel().indexOf(strIndex) !== 0) {
                    tab.setLabel(strIndex + tab.getLabel());
                    if (index == selectedTabIndex) {
                        this.foldButton.setLabel(tab.getLabel());
                    }
                } else {
                    tab.setLabel(tab.getLabel().replace(strIndex, ""));
                }
            });
        }

        checkAndMinimize() {
            const needUpdate = () => this.isStepNavigatorFit() == this.hasClass('minimized');

            if (needUpdate()) {
                const needMinimize = !this.hasClass('minimized');
                this.toggleClass('minimized', needMinimize);
                if (needMinimize) {
                    this.removeChild(this.stepNavigator);
                    this.foldButton.push(this.stepNavigator, 300);
                } else {
                    this.foldButton.pop();
                    this.stepNavigator.insertAfterEl(this.foldButton);
                }
                this.updateStepLabels(needMinimize);
            }
        }
    }
}
