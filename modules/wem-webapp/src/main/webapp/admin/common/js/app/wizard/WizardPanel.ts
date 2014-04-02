module api.app.wizard {

    export interface WizardPanelParams {

        tabId:api.app.AppBarTabId;

        persistedItem:any;

        formIcon:FormIcon;

        mainToolbar:api.ui.toolbar.Toolbar;

        stepToolbar?:api.ui.toolbar.Toolbar;

        header:WizardHeader;

        actions:WizardActions<any>;

        livePanel?:api.ui.Panel;

        steps:api.app.wizard.WizardStep[];
    }

    export class WizardPanel<T> extends api.ui.Panel implements api.ui.Closeable, api.ui.ActionContainer {

        private tabId: api.app.AppBarTabId;

        private persistedItem: T;

        private mainToolbar: api.ui.toolbar.Toolbar;

        private stepToolbar: api.ui.toolbar.Toolbar;

        private actions: WizardActions<T>;

        private header: WizardHeader;

        private stepNavigator: WizardStepNavigator;

        private stepPanels: api.app.wizard.WizardStepDeckPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged: boolean = true;

        private renderingNew: boolean;

        private firstShow: boolean;

        private closedListeners: {(event: WizardClosedEvent):void}[] = [];

        private backPanel: api.ui.DeckPanel;

        private formPanel: api.ui.Panel;

        private lastFocusedElement: JQuery;

        private stepNavigatorAndToolbarContainer: api.dom.DivEl;

        private new: boolean;

        constructor(params: WizardPanelParams, callback: Function) {
            super("wizard-panel");

            this.tabId = params.tabId;
            this.persistedItem = params.persistedItem;
            this.new = params.persistedItem == null;
            this.header = params.header;
            this.mainToolbar = params.mainToolbar;
            this.stepToolbar = params.stepToolbar;
            this.actions = params.actions;

            this.formPanel = new api.ui.Panel("form-panel");
            $(this.formPanel.getHTMLElement()).scroll(() => this.updateStickyToolbar());

            this.backPanel = new api.ui.DeckPanel("wizard-back-panel");
            this.backPanel.addPanel(this.formPanel);
            this.backPanel.showPanel(0);

            this.appendChild(this.mainToolbar);
            this.appendChild(this.backPanel);

            var aboveStepPanels = new api.dom.DivEl();
            this.formPanel.appendChild(aboveStepPanels);

            aboveStepPanels.appendChild(params.formIcon);

            aboveStepPanels.appendChild(this.header);

            this.stepNavigatorAndToolbarContainer = new api.dom.DivEl("wizard-step-navigator-and-toolbar");
            this.stepNavigator = new WizardStepNavigator();
            if (this.stepToolbar) {
                this.stepNavigatorAndToolbarContainer.appendChild(this.stepToolbar);
            }
            this.stepNavigatorAndToolbarContainer.appendChild(this.stepNavigator);
            aboveStepPanels.appendChild(this.stepNavigatorAndToolbarContainer);

            this.stepPanels = new WizardStepDeckPanel(this.stepNavigator);
            this.formPanel.appendChild(this.stepPanels);

            if (params.livePanel) {
                this.backPanel.addPanel(params.livePanel);
            }

            this.setSteps(params.steps);

            if (this.persistedItem != null) {

                this.setPersistedItem(this.persistedItem).
                    then(() => {
                        return this.postRenderExisting(this.persistedItem);
                    }).finally(() => {
                        callback();
                    }).done();
            }
            else {
                this.preRenderNew().
                    then(() => {
                        return this.renderNew();
                    }).then(() => {
                        return this.postRenderNew();
                    }).finally(()=> {
                        callback();
                    }).done();
            }

            this.onRendered((event: api.dom.ElementRenderedEvent) => {

                this.firstShow = true;
            });
            this.onShown((event: api.dom.ElementShownEvent) => {

                if (this.firstShow) {
                    this.firstShow = false;
                    this.giveInitialFocus();
                }

                if (this.lastFocusedElement) {
                    this.lastFocusedElement.focus();
                }
            });
        }

        isNew(): boolean {
            return this.new;
        }

        updateStickyToolbar() {
            var scrollTop = $('.form-panel').scrollTop();
            var wizardHeaderHeight = this.header.getEl().getHeightWithMargin() + this.header.getEl().getOffsetTopRelativeToParent();
            if (scrollTop > wizardHeaderHeight) {
                this.mainToolbar.removeClass("scrolling");
                this.stepNavigatorAndToolbarContainer.addClass("scroll-stick");
            } else if (scrollTop < wizardHeaderHeight) {
                this.mainToolbar.addClass("scrolling");
                this.stepNavigatorAndToolbarContainer.removeClass("scroll-stick");
                // do render to account for sticky toolbar
                this.formPanel.render();
            }
            if (scrollTop == 0) {
                this.mainToolbar.removeClass("scrolling");
            }
        }

        giveInitialFocus() {
            this.header.giveFocus();

            this.startRememberFocus();
        }

        startRememberFocus() {
            jQuery(this.getHTMLElement()).on("focus", "*", (e) => {
                e.stopPropagation();
                this.lastFocusedElement = jQuery(e.target);
            });
        }

        showPanel(panel: api.ui.Panel) {
            this.backPanel.showPanel(this.backPanel.getPanelIndex(panel));
        }

        showMainPanel() {
            this.backPanel.showPanel(0);
        }

        getTabId(): api.app.AppBarTabId {
            return this.tabId;
        }

        getHeader(): WizardHeader {
            return this.header;
        }

        getIconUrl(): string {
            return null; // TODO:
        }

        getActions(): api.ui.Action[] {
            return this.mainToolbar.getActions();
        }

        private setSteps(steps: api.app.wizard.WizardStep[]) {

            steps.forEach((step: api.app.wizard.WizardStep, index: number) => {
                this.stepPanels.addNavigablePanelToBack(step.getTabBarItem(), step.getPanel());
                // Ensure first step is shown
                if (index == 0) {
                    this.stepPanels.showPanel(0);
                }
            });
        }

        preRenderNew(): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of renderNew
            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        isRenderingNew(): boolean {
            return this.renderingNew;
        }

        renderNew(): Q.Promise<void> {

            var deferred = Q.defer<void>();
            this.renderingNew = true;
            this.actions.enableActionsForNew();
            deferred.resolve(null);
            return deferred.promise;
        }

        postRenderNew(): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of renderNew
            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        private setPersistedItem(persistedItem: T): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.renderingNew = false;
            this.persistedItem = persistedItem;
            this.actions.enableActionsForExisting(persistedItem);

            this.layoutPersistedItem(persistedItem).
                then(() => {
                    deferred.resolve(null)
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }

        layoutPersistedItem(persistedItem: T): Q.Promise<void> {

            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        postRenderExisting(existing: T): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of setPersistedItem
            var deferred = Q.defer<void>();

            deferred.resolve(null);
            return deferred.promise;
        }

        getPersistedItem(): T {
            return this.persistedItem;
        }

        isItemPersisted(): boolean {
            return this.persistedItem != null;
        }

        /*
         * Override this method in specific wizard to do proper check.
         */
        hasUnsavedChanges(): boolean {
            return this.isChanged;
        }

        askUserForSaveChangesBeforeClosing() {
            new api.app.wizard.SaveBeforeCloseDialog(this).open();
        }

        saveChanges(): Q.Promise<T> {

            var deferred = Q.defer<T>();

            if (this.isItemPersisted()) {
                this.new = false;
                this.updatePersistedItem().
                    then((persisted: T) => {
                        this.setPersistedItem(persisted).
                            then(() => {

                                deferred.resolve(persisted);
                            }).catch((reason) => {
                                deferred.reject(reason);
                            }).done();

                    }).catch((reason) => {
                        deferred.reject(reason);
                    }).done();
            }
            else {
                this.persistNewItem().
                    then((persistedItem: T)=> {

                        this.postPersistNewItem(persistedItem).
                            then(()=> {
                                return this.setPersistedItem(persistedItem);
                            }).then(() => {
                                deferred.resolve(persistedItem);
                            }).catch((reason) => {
                                deferred.reject(reason);
                            }).done();

                    }).catch((reason) => {
                        deferred.reject(reason);
                    }).done();
            }

            this.isChanged = false;

            return deferred.promise;
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(): Q.Promise<T> {
            throw new Error("Must be overriden by inheritor");
        }

        postPersistNewItem(persistedItem: T): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of persistNewItem
            var deferred = Q.defer<void>();

            deferred.resolve(null);
            return deferred.promise;
        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(): Q.Promise<T> {
            throw new Error("Must be overriden by inheritor");
        }

        close(checkCanClose: boolean = false) {

            if (checkCanClose) {
                if (this.canClose()) {
                    this.closing();
                }
            }
            else {
                this.closing();
            }
        }

        canClose(): boolean {

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

        onClosed(listener: (event: WizardClosedEvent)=>void) {
            this.closedListeners.push(listener);
        }

        unClosed(listener: (event: WizardClosedEvent)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: WizardClosedEvent)=>void) => {
                return currentListener != listener;
            });
        }

        private notifyClosed() {
            this.closedListeners.forEach((listener: (event: WizardClosedEvent)=>void) => {
                listener.call(this, new WizardClosedEvent(this));
            });
        }
    }
}