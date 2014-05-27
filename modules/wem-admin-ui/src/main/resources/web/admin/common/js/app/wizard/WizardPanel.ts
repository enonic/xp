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

        split?:boolean;
    }

    export class WizardPanel<EQUITABLE extends api.Equitable> extends api.ui.Panel implements api.ui.Closeable, api.ui.ActionContainer {

        private tabId: api.app.AppBarTabId;

        private persistedItem: EQUITABLE;

        private mainToolbar: api.ui.toolbar.Toolbar;

        private stepToolbar: api.ui.toolbar.Toolbar;

        private actions: WizardActions<EQUITABLE>;

        private header: WizardHeader;

        private stepNavigator: WizardStepNavigator;

        private stepPanels: api.app.wizard.WizardStepsPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged: boolean = true;

        private renderingNew: boolean;

        private firstShow: boolean;

        private closedListeners: {(event: WizardClosedEvent):void}[] = [];

        private formPanel: api.ui.Panel;

        private lastFocusedElement: JQuery;

        private stepNavigatorAndToolbarContainer: api.dom.DivEl;

        private persisted: boolean;

        private splitPanel: api.ui.SplitPanel;

        private splitPanelThreshold: number = 960;

        constructor(params: WizardPanelParams, callback: Function) {
            super("wizard-panel");

            this.tabId = params.tabId;
            this.persistedItem = params.persistedItem;
            this.persisted = params.persistedItem == null;
            this.header = params.header;
            this.mainToolbar = params.mainToolbar;
            this.stepToolbar = params.stepToolbar;
            this.actions = params.actions;

            this.formPanel = new api.ui.Panel("form-panel");
            this.formPanel.onScroll(() => this.updateStickyToolbar());

            this.appendChild(this.mainToolbar);
            if (params.split && params.livePanel) {
                this.splitPanel = new api.ui.SplitPanelBuilder(this.formPanel, params.livePanel)
                    .setFirstPanelMinSize(280)
                    .setAlignment(api.ui.SplitPanelAlignment.VERTICAL)
                    .build();
                this.updateSplitPanel();
                this.splitPanel.onResized((event: api.dom.ElementResizedEvent) => {
                    this.updateStickyToolbar();
                });
                this.appendChild(this.splitPanel);
            } else {
                this.appendChild(this.formPanel);
            }


            var aboveStepPanels = new api.dom.DivEl();
            aboveStepPanels.appendChild(params.formIcon);
            aboveStepPanels.appendChild(this.header);

            var container = new api.dom.DivEl("test-container");
            this.stepNavigatorAndToolbarContainer = new api.dom.DivEl("wizard-step-navigator-and-toolbar");
            this.stepNavigator = new WizardStepNavigator();
            if (this.stepToolbar) {
                this.stepNavigatorAndToolbarContainer.appendChild(this.stepToolbar);
            }
            this.stepNavigatorAndToolbarContainer.appendChild(this.stepNavigator);
            aboveStepPanels.appendChild(this.stepNavigatorAndToolbarContainer);

            this.stepPanels = new WizardStepsPanel(this.stepNavigator, this.formPanel);
            this.formPanel.appendChild(aboveStepPanels).appendChild(this.stepPanels);

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

            api.dom.Window.get().onResized((event: UIEvent) => {

                if (!event.srcElement.nodeName) {
                    this.updateStickyToolbar();
                }
            }, this);
        }

        isPersisted(): boolean {
            return this.persisted;
        }

        updateStickyToolbar() {
            var scrollTop = wemjq('.form-panel').scrollTop();
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
            wemjq(this.getHTMLElement()).on("focus", "*", (e) => {
                e.stopPropagation();
                this.lastFocusedElement = wemjq(e.target);
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
                this.stepPanels.addNavigablePanel(step.getTabBarItem(), step.getPanel());
                // Ensure first step is shown
                if (index == 0) {
                    this.stepPanels.showPanelByIndex(0);
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

        private setPersistedItem(persistedItem: EQUITABLE): Q.Promise<void> {

            this.renderingNew = false;
            this.persistedItem = persistedItem;
            this.actions.enableActionsForExisting(persistedItem);

            return this.layoutPersistedItem(persistedItem);
        }

        layoutPersistedItem(persistedItem: EQUITABLE): Q.Promise<void> {

            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        postRenderExisting(existing: EQUITABLE): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of setPersistedItem
            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        getPersistedItem(): EQUITABLE {
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

        saveChanges(): Q.Promise<EQUITABLE> {

            if (this.isItemPersisted()) {
                this.persisted = false;
                return this.updatePersistedItem().
                    then((persisted: EQUITABLE) => {

                        this.isChanged = false;
                        return this.setPersistedItem(persisted).
                            then(() => persisted);

                    });
            }
            else {
                return this.persistNewItem().
                    then((persistedItem: EQUITABLE)=> {

                        this.isChanged = false;
                        return this.postPersistNewItem(persistedItem).
                            then(()=> {

                                return this.setPersistedItem(persistedItem);

                            }).then(() => persistedItem);

                    });
            }
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(): Q.Promise<EQUITABLE> {
            throw new Error("Must be overriden by inheritor");
        }

        postPersistNewItem(persistedItem: EQUITABLE): Q.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of persistNewItem
            var deferred = Q.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(): Q.Promise<EQUITABLE> {
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

        getSplitPanel(): api.ui.SplitPanel {
            return this.splitPanel;
        }

        private updateSplitPanel() {
            if (wemjq(window).width() > this.splitPanelThreshold) {
                this.splitPanel.setFirstPanelSize("30%");
            }
            this.splitPanel.distribute();
        }

        private notifyClosed() {
            this.closedListeners.forEach((listener: (event: WizardClosedEvent)=>void) => {
                listener.call(this, new WizardClosedEvent(this));
            });
        }
    }
}