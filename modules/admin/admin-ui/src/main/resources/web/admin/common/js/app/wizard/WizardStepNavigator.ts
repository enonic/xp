module api.app.wizard {

    import KeyBinding = api.ui.KeyBinding;
    import KeyBindings = api.ui.KeyBindings;
    import TabBarItem = api.ui.tab.TabBarItem;

    export class WizardStepNavigator extends api.ui.tab.TabBar {

        constructor() {
            super('wizard-step-navigator');
        }

        insertNavigationItem(tab: TabBarItem, index: number, silent?: boolean) {
            super.insertNavigationItem(tab, index, silent);

            this.addKeyNavigation(tab);
        }

        addNavigationItem(step: api.ui.tab.TabBarItem) {
            super.addNavigationItem(step);

            if (this.getSize() === 1) {
                step.addClass('first');
            }
        }

        nextStep() {
            let nextIndex = Math.min(this.getSelectedIndex() + 1, this.getSize() - 1);
            this.selectNavigationItem(nextIndex);
        }

        previousStep() {
            let previousIndex = Math.max(this.getSelectedIndex() - 1, 0);
            this.selectNavigationItem(previousIndex);
        }

        hasNext(): boolean {
            return this.getSelectedIndex() < this.getSize() - 1;
        }

        hasPrevious(): boolean {
            return this.getSelectedIndex() > 0;
        }

        private addKeyNavigation(tab: TabBarItem) {
            const combination: string = 'alt+' + this.getSize();
            const keyBinding: KeyBinding = new KeyBinding(combination, () => {
                const isTabVisible: boolean = tab.getHTMLElement().style.display !== 'none';
                const tabIndexToShow: number = isTabVisible ? tab.getIndex() : tab.getIndex() + 1;

                this.selectNavigationItem(tabIndexToShow);
            });

            KeyBindings.get().bindKey(keyBinding);
        }

    }
}
