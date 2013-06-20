module api{

    export class AppPanel extends api_ui_tab.TabbedDeckPanel {

        private homePanel:api_ui.Panel;

        constructor(appBar:api_ui_tab.TabNavigator, homePanel:api_ui.Panel) {
            super(appBar);

            this.homePanel = homePanel;
            var homePanelMenuItem = new api_appbar.AppBarTabMenuItem("home");
            homePanelMenuItem.setVisible(false);
            homePanelMenuItem.setRemovable(false);
            this.addTab(homePanelMenuItem, this.homePanel);
            this.showPanel(0);
        }

        showBrowsePanel() {
            this.showPanel(0);
        }


    }

    export class AppDeckPanel extends api_ui_tab.TabbedDeckPanel {

        private appPanel:api.AppPanel;

        constructor(navigator:api_ui_tab.TabNavigator) {
            super(navigator);
        }


        tabRemove(tab:api_ui_tab.Tab):bool {

            if (this.hasUnsavedChanges()) {
                return false;
            }
            else {
                return super.tabRemove(tab);
            }

        }

        removePanel(index:number):api_ui.Panel {
            var panelRemoved = super.removePanel(index);
            if (this.getSize() == 0) {
                this.appPanel.showBrowsePanel();
            }
            return panelRemoved;
        }


        private hasUnsavedChanges():bool {
            /*TODO: if (wizardPanel != null && wizardPanel.getWizardDirty()) {
             Ext.Msg.confirm('Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
             (answer) => {
             if ('yes' === answer) {
             this.removeTab(tab);
             } else {
             return false;
             }
             });
             } else {
             this.removeTab(tab);
             }*/
            return false;
        }

        setAppPanel(value:api.AppPanel):void {
            this.appPanel = value;
        }
    }
}
