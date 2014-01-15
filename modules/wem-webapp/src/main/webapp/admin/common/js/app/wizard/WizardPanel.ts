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

    export class WizardPanel<T> extends api.ui.Panel implements api.ui.Closeable, api.event.Observable, api.ui.ActionContainer {

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

        private previous: WizardStepNavigationArrow;

        private next: WizardStepNavigationArrow;

        private listeners: WizardPanelListener[] = [];

        private backPanel: api.ui.DeckPanel;

        private formPanel: api.ui.Panel;

        private lastFocusedElement: JQuery;

        private stepNavigatorAndToolbarContainer: api.dom.DivEl;

        constructor(params: WizardPanelParams, callback: Function) {
            super(true);

            console.log("WizardPanel.constructor started");

            this.tabId = params.tabId;
            this.persistedItem = params.persistedItem;
            this.header = params.header;
            this.mainToolbar = params.mainToolbar;
            this.stepToolbar = params.stepToolbar;
            this.actions = params.actions;

            this.getEl().addClass("wizard-panel");
            this.backPanel = new api.ui.DeckPanel(true);
            this.backPanel.addClass("wizard-back-panel");
            this.formPanel = new api.ui.Panel(true);
            this.formPanel.addClass("form-panel");

            this.backPanel.addPanel(this.formPanel);
            this.backPanel.showPanel(0);

            this.appendChild(this.mainToolbar);
            this.appendChild(this.backPanel);

            var aboveStepPanels = new api.dom.DivEl();
            this.formPanel.appendChild(aboveStepPanels);

            aboveStepPanels.appendChild(params.formIcon);

            aboveStepPanels.appendChild(this.header);

            this.stepNavigatorAndToolbarContainer = new api.dom.DivEl(true, "wizard-step-navigator-and-toolbar");
            this.stepNavigator = new WizardStepNavigator();
            if (this.stepToolbar) {
                this.stepNavigatorAndToolbarContainer.appendChild(this.stepToolbar);
            }
            this.stepNavigatorAndToolbarContainer.appendChild(this.stepNavigator);
            aboveStepPanels.appendChild(this.stepNavigatorAndToolbarContainer);

            this.stepPanels = new WizardStepDeckPanel(this.stepNavigator);
            this.formPanel.appendChild(this.stepPanels);

            this.previous = new WizardStepNavigationArrow(WizardStepNavigationArrow.PREVIOUS, this.stepNavigator);
            this.next = new WizardStepNavigationArrow(WizardStepNavigationArrow.NEXT, this.stepNavigator);
            this.formPanel.appendChild(this.previous);
            this.formPanel.appendChild(this.next);

            if (params.livePanel) {
                this.backPanel.addPanel(params.livePanel);
            }

            this.setSteps(params.steps);

            if (this.persistedItem != null) {

                this.setPersistedItem(this.persistedItem).
                    done(() => {

                        this.postRenderExisting(this.persistedItem).
                            done(() => {

                                console.log("WizardPanel.constructor finished");
                                callback();
                            });
                    });
            }
            else {
                this.preRenderNew().
                    then(() => {

                        this.renderNew().
                            then(() => {

                                this.postRenderNew().
                                    then(()=> {

                                        console.log("WizardPanel.constructor finished");
                                        callback();
                                    });
                            });
                    });
            }
        }

        onElementShown() {
            console.log("WizardPanel.onElementShown");

            if (this.lastFocusedElement) {
                console.log("Last focused element was remembered: ", this.lastFocusedElement);
                this.lastFocusedElement.focus();
            }
        }

        afterRender() {
            super.afterRender();
            this.stepPanels.afterRender();
            this.backPanel.afterRender();

            $('.form-panel').scroll(() => {
                var scrollTop = $('.form-panel').scrollTop();
                var wizardHeaderHeight = this.header.getEl().getHeightWithMargin() + this.header.getEl().getOffsetTop();
                if (scrollTop > wizardHeaderHeight) {
                    this.mainToolbar.removeClass("scrolling");
                    this.stepNavigatorAndToolbarContainer.addClass("scroll-stick");
                } else if (scrollTop < wizardHeaderHeight) {
                    this.mainToolbar.addClass("scrolling");
                    this.stepNavigatorAndToolbarContainer.removeClass("scroll-stick");
                }
                if (scrollTop == 0) {
                    this.mainToolbar.removeClass("scrolling");
                }
            });
        }

        initWizardPanel() {
            console.log("WizardPanel.initWizardPanel");
            this.giveInitialFocus();
        }

        giveInitialFocus() {
            console.log("WizardPanel.giveInitialFocus");
            this.header.giveFocus();

            this.startRememberFocus();
        }

        startRememberFocus() {
            jQuery(this.getHTMLElement()).on("focus", "*", (e) => {
                e.stopPropagation();
                this.lastFocusedElement = jQuery(e.target);
            });
        }

        toggleFormPanel(toggle: boolean) {
            if (toggle) {
                this.backPanel.showPanel(0)
            } else {
                this.backPanel.showPanel(1)
            }
        }

        addListener(listener: WizardPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: WizardPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
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
            console.log("WizardPanel.renderNew");

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
            console.log("WizardPanel.setPersistedItem");

            var deferred = Q.defer<void>();

            this.renderingNew = false;
            this.persistedItem = persistedItem;
            this.actions.enableActionsForExisting(persistedItem);

            this.layoutPersistedItem(persistedItem).
                done(() => {
                    deferred.resolve(null)
                });

            return deferred.promise;
        }

        layoutPersistedItem(persistedItem: T): Q.Promise<void> {
            console.log("WizardPanel.layoutPersistedItem");

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
                this.updatePersistedItem().
                    done((persisted: T) => {
                        this.setPersistedItem(persisted).
                            done(() => {

                                deferred.resolve(persisted);
                            });
                    });
            }
            else {
                this.persistNewItem().
                    done((persistedItem: T)=> {

                        this.postPersistNewItem(persistedItem).
                            done(()=> {

                            this.setPersistedItem(persistedItem).
                                done(() => {
                                    deferred.resolve(persistedItem);
                                });
                        });

                    });
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

        private notifyClosed() {
            this.listeners.forEach((listener: WizardPanelListener) => {
                if (listener.onClosed) {
                    listener.onClosed(this);
                }
            });
        }
    }
}