module api_app_wizard {

    export interface WizardPanelParams {

        formIcon:FormIcon;

        toolbar:api_ui_toolbar.Toolbar;

        header:WizardHeader;

        livePanel?:api_ui.Panel;
    }

    export class WizardPanel extends api_ui.Panel implements api_ui.Closeable, api_event.Observable, api_ui.ActionContainer {

        private persistedItem:any;

        private toolbar:api_ui_toolbar.Toolbar;

        private header:WizardHeader;

        private stepNavigator:WizardStepNavigator;

        private stepPanels:api_app_wizard.WizardStepDeckPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged:boolean = true;

        private previous:WizardStepNavigationArrow;

        private next:WizardStepNavigationArrow;

        private listeners:WizardPanelListener[] = [];

        private backPanel:api_ui.DeckPanel;
        private formPanel:api_ui.Panel;


        constructor(params:WizardPanelParams) {
            super("WizardPanel");
            this.getEl().addClass("wizard-panel");
            this.backPanel = new api_ui.DeckPanel("BackPanel");
            this.formPanel = new api_ui.Panel("FormPanel");

            this.backPanel.addPanel(this.formPanel);
            this.backPanel.showPanel(0);
            this.toolbar = params.toolbar;

            this.appendChild(this.toolbar);
            this.appendChild(this.backPanel);

            this.formPanel.appendChild(params.formIcon);

            this.header = params.header;
            this.formPanel.appendChild(this.header);

            this.stepNavigator = new WizardStepNavigator();
            this.stepPanels = new WizardStepDeckPanel(this.stepNavigator);
            this.formPanel.appendChild(this.stepNavigator);
            this.formPanel.appendChild(this.stepPanels);

            this.previous = new WizardStepNavigationArrow(WizardStepNavigationArrow.PREVIOUS, this.stepNavigator);
            this.next = new WizardStepNavigationArrow(WizardStepNavigationArrow.NEXT, this.stepNavigator);
            this.formPanel.appendChild(this.previous);
            this.formPanel.appendChild(this.next);

            if (params.livePanel) {
                this.backPanel.addPanel(params.livePanel);
            }
        }

        toggleFormPanel(toggle:boolean) {
            if (toggle) {
                this.backPanel.showPanel(0)
            } else {
                this.backPanel.showPanel(1)
            }
        }

        afterRender() {
            super.afterRender();
            this.stepPanels.afterRender();
            this.backPanel.afterRender();
        }

        addListener(listener:WizardPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:WizardPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        getHeader():WizardHeader {
            return this.header;
        }

        getActions():api_ui.Action[] {
            return this.toolbar.getActions();
        }

        private notifyClosed() {
            this.listeners.forEach((listener:WizardPanelListener) => {
                if (listener.onClosed) {
                    listener.onClosed(this);
                }
            });
        }

        setPersistedItem(item:any) {
            this.persistedItem = item;
        }


        isItemPersisted():boolean {
            return this.persistedItem != null;
        }

        getIconUrl():string {
            return null; // TODO:
        }

        addStep(step:api_ui.PanelNavigationItem, panel: api_ui.Panel, inBackground:boolean = true) {
            this.stepPanels.addNavigationItem(step, panel, inBackground);
        }

        close(checkCanClose:boolean = false) {

            if (checkCanClose) {
                if (this.canClose()) {
                    this.closing();
                }
            }
            else {
                this.closing();
            }
        }

        canClose():boolean {

            if (this.hasUnsavedChanges()) {
                this.askUserForSaveChangesBeforeClosing();
                return false;
            }
            else {
                return true;
            }
        }

        closing() {
            this.notifyClosed();
        }

        /*
         * Override this method in specific wizard to do proper check.
         */
        hasUnsavedChanges():boolean {
            return this.isChanged;
        }

        askUserForSaveChangesBeforeClosing() {
            new api_app_wizard.SaveBeforeCloseDialog(this).open();
        }

        saveChanges(successCallback?:() => void) {

            if (this.isItemPersisted()) {
                this.updatePersistedItem(successCallback);
            }
            else {
                this.persistNewItem(successCallback);
            }

            this.isChanged = false;
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(successCallback?:() => void) {

        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(successCallback?:() => void) {

        }
    }
}