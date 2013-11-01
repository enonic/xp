module api_app_wizard {

    export interface WizardPanelParams {

        formIcon:FormIcon;

        toolbar:api_ui_toolbar.Toolbar;

        header:WizardHeader;

        actions:WizardActions<any>;

        livePanel?:api_ui.Panel;
    }

    export class WizardPanel<T> extends api_ui.Panel implements api_ui.Closeable, api_event.Observable, api_ui.ActionContainer {

        private persistedItem:T;

        private toolbar:api_ui_toolbar.Toolbar;

        private actions:WizardActions<T>;

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

        private focusElement:JQuery;


        constructor(params:WizardPanelParams) {
            super("WizardPanel");

            this.header = params.header;
            this.toolbar = params.toolbar;
            this.actions = params.actions;

            this.getEl().addClass("wizard-panel");
            this.backPanel = new api_ui.DeckPanel("WizardBackPanel");
            this.backPanel.addClass("wizard-back-panel");
            this.formPanel = new api_ui.Panel("FormPanel");
            this.formPanel.addClass("form-panel");

            this.backPanel.addPanel(this.formPanel);
            this.backPanel.showPanel(0);

            this.appendChild(this.toolbar);
            this.appendChild(this.backPanel);

            var aboveStepPanels = new api_dom.DivEl();
            this.formPanel.appendChild(aboveStepPanels);

            aboveStepPanels.appendChild(params.formIcon);

            aboveStepPanels.appendChild(this.header);

            this.stepNavigator = new WizardStepNavigator();
            aboveStepPanels.appendChild(this.stepNavigator);

            this.stepPanels = new WizardStepDeckPanel(this.stepNavigator);
            this.formPanel.appendChild(this.stepPanels);

            this.previous = new WizardStepNavigationArrow(WizardStepNavigationArrow.PREVIOUS, this.stepNavigator);
            this.next = new WizardStepNavigationArrow(WizardStepNavigationArrow.NEXT, this.stepNavigator);
            this.formPanel.appendChild(this.previous);
            this.formPanel.appendChild(this.next);

            if (params.livePanel) {
                this.backPanel.addPanel(params.livePanel);
            }

            jQuery(this.getHTMLElement()).on("focus", "*", (e) => {
                e.stopPropagation();
                this.focusElement = jQuery(e.target);
            });
        }

        showCallback() {
            if (this.focusElement) {
                this.focusElement.focus();
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

        renderNew() {
            this.actions.enableActionsForNew();
        }

        setPersistedItem(item:T) {
            this.persistedItem = item;
            this.actions.enableActionsForExisting(item);
        }


        isItemPersisted():boolean {
            return this.persistedItem != null;
        }

        getIconUrl():string {
            return null; // TODO:
        }

        addStep(step:api_ui.PanelNavigationItem, panel:api_ui.Panel) {
            this.stepPanels.addNavigablePanelToBack(step, panel);

            // Ensure first step is shown
            if( this.stepPanels.getSize() == 1 ) {
                this.stepPanels.showPanel( 0 );
            }
        }

        showFirstStep() {
            this.stepPanels.showPanel(0);
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