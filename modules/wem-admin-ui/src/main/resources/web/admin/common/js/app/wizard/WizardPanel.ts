module api.app.wizard {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export interface WizardPanelParams {

        tabId:api.app.AppBarTabId;

        persistedItem:any;

        formIcon:FormIcon;

        mainToolbar:api.ui.toolbar.Toolbar;

        stepToolbar?:api.ui.toolbar.Toolbar;

        header:WizardHeader;

        actions:WizardActions<any>;

        livePanel?:api.ui.panel.Panel;

        steps:api.app.wizard.WizardStep[];

        split?:boolean;
    }

    export class WizardPanel<EQUITABLE extends api.Equitable> extends api.ui.panel.Panel implements api.ui.Closeable, api.ui.ActionContainer {

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

        private formPanel: api.ui.panel.Panel;

        private lastFocusedElement: JQuery;

        private stepNavigatorAndToolbarContainer: api.dom.DivEl;

        private splitPanel: api.ui.panel.SplitPanel;

        private splitPanelThreshold: number = 960;

        private stepNavigatorPlaceholder: api.dom.DivEl;

        constructor(params: WizardPanelParams, callback: Function) {
            super("wizard-panel");

            this.tabId = params.tabId;
            this.persistedItem = params.persistedItem;
            this.header = params.header;
            this.mainToolbar = params.mainToolbar;
            this.stepToolbar = params.stepToolbar;
            this.actions = params.actions;

            this.formPanel = new api.ui.panel.Panel("form-panel");
            this.formPanel.onScroll(() => this.updateStickyToolbar());

            this.appendChild(this.mainToolbar);
            if (params.split && params.livePanel) {
                this.splitPanel = this.createSplitPanel(this.formPanel, params.livePanel);
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
            this.stepNavigatorAndToolbarContainer.onShown((event: api.dom.ElementShownEvent) => {
                // set scroll offset equal to the height of the step navigator to switch steps at the bottom of it when sticky
                this.stepPanels.setScrollOffset(event.getElement().getEl().getHeight());
            });
            ResponsiveManager.onAvailableSizeChanged(this.stepNavigatorAndToolbarContainer, (item: ResponsiveItem) => {
                // update offset if step navigator is resized
                if (this.isVisible()) {
                    this.updateStickyToolbar();
                    this.stepPanels.setScrollOffset(item.getElement().getEl().getHeight());
                }
            });
            this.formPanel.appendChild(aboveStepPanels).appendChild(this.stepPanels);

            this.setSteps(params.steps);

            if (this.persistedItem != null) {
                this.setPersistedItem(this.persistedItem).
                    then(() => this.postRenderExisting(this.persistedItem)).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    finally(() => callback()).
                    done();
            } else {
                this.preRenderNew().
                    then(() => this.renderNew()).
                    then(() => this.postRenderNew()).
                    catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                    finally(()=> callback()).
                    done();
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

        updateStickyToolbar() {
            var scrollTop = this.formPanel.getHTMLElement().scrollTop;
            var wizardHeaderHeight = this.header.getEl().getHeightWithMargin() + this.header.getEl().getOffsetTopRelativeToParent();
            if (scrollTop > wizardHeaderHeight) {
                this.mainToolbar.removeClass("scroll-shadow");
                var stepNavigatorEl = this.stepNavigatorAndToolbarContainer.getEl().addClass("scroll-stick");
                if (!this.stepNavigatorPlaceholder) {
                    this.stepNavigatorPlaceholder = new api.dom.DivEl('toolbar-placeholder');
                    this.stepNavigatorPlaceholder.insertAfterEl(this.stepNavigatorAndToolbarContainer);
                    this.stepNavigatorPlaceholder.getEl().setWidthPx(stepNavigatorEl.getWidth()).setHeightPx(stepNavigatorEl.getHeight());
                }
            } else if (scrollTop < wizardHeaderHeight) {
                this.mainToolbar.addClass("scroll-shadow");
                this.stepNavigatorAndToolbarContainer.removeClass("scroll-stick");
                if (this.stepNavigatorPlaceholder) {
                    this.stepNavigatorPlaceholder.remove();
                    this.stepNavigatorPlaceholder = undefined;
                }
            }
            if (scrollTop == 0) {
                this.mainToolbar.removeClass("scroll-shadow");
            }
            this.stepNavigatorAndToolbarContainer.getEl().setWidthPx(this.formPanel.getEl().getWidth());
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

        setTabId(tabId: api.app.AppBarTabId) {
            this.tabId = tabId;
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
                return this.updatePersistedItem().then((persistedItem: EQUITABLE) => {

                    this.isChanged = false;
                    return this.setPersistedItem(persistedItem).
                        then(() => persistedItem);
                });
            } else {
                return this.persistNewItem().then((persistedItem: EQUITABLE) => {

                    this.isChanged = false;
                    return this.postPersistNewItem(persistedItem).
                        then(() => this.setPersistedItem(persistedItem)).
                        then(() => persistedItem);
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
            if (!checkCanClose || this.canClose()) {
                this.notifyClosed();
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

        onClosed(listener: (event: WizardClosedEvent)=>void) {
            this.closedListeners.push(listener);
        }

        unClosed(listener: (event: WizardClosedEvent)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: WizardClosedEvent)=>void) => {
                return currentListener != listener;
            });
        }

        getSplitPanel(): api.ui.panel.SplitPanel {
            return this.splitPanel;
        }

        public setLivePanel(livePanel: api.ui.panel.Panel) {
            if (this.splitPanel) {
                this.removeChild(this.splitPanel);
            }
            this.splitPanel = this.createSplitPanel(this.formPanel, livePanel);
            this.appendChild(this.splitPanel);
        }

        private createSplitPanel(firstPanel: api.ui.panel.Panel, secondPanel: api.ui.panel.Panel): api.ui.panel.SplitPanel {
            var splitPanel = new api.ui.panel.SplitPanelBuilder(firstPanel, secondPanel)
                .setFirstPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL)
                .setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL)
                .build();
            this.updateSplitPanel(splitPanel);
            return splitPanel;
        }

        private updateSplitPanel(splitPanel: api.ui.panel.SplitPanel) {
            if (wemjq(window).width() > this.splitPanelThreshold) {
                splitPanel.setFirstPanelSize(38, api.ui.panel.SplitPanelUnit.PERCENT);
            }
            splitPanel.distribute();
        }

        private notifyClosed() {
            this.closedListeners.forEach((listener: (event: WizardClosedEvent)=>void) => {
                listener(new WizardClosedEvent(this));
            });
        }
    }
}