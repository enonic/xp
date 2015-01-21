module api.app {

    export interface BrowseBasedAppPanelConfig<M> {

        appBar:api.app.bar.AppBar;

    }

    export class BrowseAndWizardBasedAppPanel<M extends api.Equitable> extends AppPanel<M> {

        private appBarTabMenu: api.app.bar.AppBarTabMenu;

        private currentKeyBindings: api.ui.KeyBinding[];

        private appBar: api.app.bar.AppBar;

        constructor(config: BrowseBasedAppPanelConfig<M>) {
            super(config.appBar.getTabMenu());

            this.appBar = config.appBar;

            this.appBarTabMenu = config.appBar.getTabMenu();

            this.onPanelShown((event: api.ui.panel.PanelShownEvent) => {
                if (event.getPanel() === this.getBrowsePanel()) {
                    this.getBrowsePanel().refreshFilter();
                }

                var previousActions = this.resolveActions(event.getPreviousPanel());
                api.ui.KeyBindings.get().unbindKeys(api.ui.Action.getKeyBindings(previousActions));

                var nextActions = this.resolveActions(event.getPanel());
                this.currentKeyBindings = api.ui.Action.getKeyBindings(nextActions);
                api.ui.KeyBindings.get().bindKeys(this.currentKeyBindings);
            });
        }

        addBrowsePanel(browsePanel: api.app.browse.BrowsePanel<M>) {
            super.addBrowsePanel(browsePanel);

            this.currentKeyBindings = api.ui.Action.getKeyBindings(this.resolveActions(browsePanel));
            this.activateCurrentKeyBindings();
        }

        activateCurrentKeyBindings() {

            if (this.currentKeyBindings) {
                api.ui.KeyBindings.get().bindKeys(this.currentKeyBindings);
            }
        }

        getAppBarTabMenu(): api.app.bar.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        addViewPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, viewPanel: api.app.view.ItemViewPanel<M>) {
            super.addNavigablePanel(tabMenuItem, viewPanel, true);

            viewPanel.onClosed((event: api.app.view.ItemViewClosedEvent<M>) => {
                this.removeNavigablePanel(event.getView(), false);
            });
        }

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
            super.addNavigablePanel(tabMenuItem, wizardPanel, true);

            wizardPanel.onClosed((event: api.app.wizard.WizardClosedEvent) => {
                this.removeNavigablePanel(event.getWizard(), false);
            });
        }

        canRemovePanel(panel: api.ui.panel.Panel): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.wizard.WizardPanel)) {
                var wizardPanel: api.app.wizard.WizardPanel<any> = <api.app.wizard.WizardPanel<any>>panel;
                return wizardPanel.canClose();
            }
            return true;
        }

        private resolveActions(panel: api.ui.panel.Panel): api.ui.Action[] {
            var actions = [];
            actions = actions.concat(this.appBar.getActions());

            if (api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.wizard.WizardPanel) ||
                api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.browse.BrowsePanel)) {
                var actionContainer: api.ui.ActionContainer = <any>panel;
                actions = actions.concat(actionContainer.getActions());
            }
            return actions;
        }
    }
}
