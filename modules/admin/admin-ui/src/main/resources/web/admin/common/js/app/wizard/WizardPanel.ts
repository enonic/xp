module api.app.wizard {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export interface WizardPanelParams {

        tabId:api.app.bar.AppBarTabId;

        persistedItem:any;

        formIcon: any;

        mainToolbar:api.ui.toolbar.Toolbar;

        stepToolbar?:api.ui.toolbar.Toolbar;

        header:WizardHeader;

        actions:WizardActions<any>;

        livePanel?:api.ui.panel.Panel;

        split?:boolean;
    }

    export class WizardPanel<EQUITABLE extends api.Equitable> extends api.ui.panel.Panel implements api.ui.Closeable, api.ui.ActionContainer {

        private tabId: api.app.bar.AppBarTabId;

        private persistedItem: EQUITABLE;

        private mainToolbar: api.ui.toolbar.Toolbar;

        protected actions: WizardActions<EQUITABLE>;

        private header: WizardHeader;

        private stepNavigator: WizardStepNavigator;

        private steps: WizardStep[];

        private stepsPanel: WizardStepsPanel;

        // TODO: @alb - Value is set to 'changed' by default to see SaveChangesBeforeCloseDialog behavior.
        private isChanged: boolean = true;

        private layingOutNew: boolean;

        private firstShow: boolean;

        private closedListeners: {(event: WizardClosedEvent):void}[] = [];

        protected formPanel: api.ui.panel.Panel;

        private lastFocusedElement: HTMLElement;

        private stepNavigatorAndToolbarContainer: WizardStepNavigatorAndToolbar;

        private splitPanel: api.ui.panel.SplitPanel;

        private splitPanelThreshold: number = 960;

        private stepNavigatorPlaceholder: api.dom.DivEl;

        private validityManager: WizardValidityManager;

        private minimizeEditButton: api.dom.DivEl;

        private minimized: boolean = false;

        private toggleMinimizeListener: (event: api.ui.ActivatedEvent) => void;

        private scrollPosition: number = 0;

        constructor(params: WizardPanelParams, onSuccess: () => void, onError?: (reason: any) => void) {
            super("wizard-panel");
            this.validityManager = new WizardValidityManager();

            this.tabId = params.tabId;
            this.persistedItem = params.persistedItem;
            this.header = params.header;
            this.mainToolbar = params.mainToolbar;
            params.stepToolbar;
            this.actions = params.actions;

            this.validityManager.setHeader(this.header);

            this.formPanel = new api.ui.panel.Panel("form-panel");
            this.formPanel.onScroll(() => this.updateStickyToolbar());

            this.appendChild(this.mainToolbar);
            if (params.split && params.livePanel) {
                this.toggleMinimizeListener = (event: api.ui.ActivatedEvent) => {
                    this.toggleMinimize(event.getIndex());
                };
                this.minimizeEditButton = new api.dom.DivEl("minimize-edit icon icon-arrow-right");
                this.formPanel.prependChild(this.minimizeEditButton);
                this.splitPanel = this.createSplitPanel(this.formPanel, params.livePanel);
                this.appendChild(this.splitPanel);
                api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this.minimizeEditButton, () => {
                    this.minimizeEditButton.getEl().setLeftPx(this.stepsPanel.getEl().getWidth());
                });
                this.minimizeEditButton.onClicked(this.toggleMinimize.bind(this, -1));
            } else {
                this.appendChild(this.formPanel);
            }


            var headerAndNavigatorContainer = new api.dom.DivEl("header-and-navigator-container");
            headerAndNavigatorContainer.appendChild(params.formIcon);
            headerAndNavigatorContainer.appendChild(this.header);

            var container = new api.dom.DivEl("test-container");
            this.stepNavigatorAndToolbarContainer = new WizardStepNavigatorAndToolbar("wizard-step-navigator-and-toolbar");
            this.stepNavigator = new WizardStepNavigator();
            if (params.stepToolbar) {
                this.stepNavigatorAndToolbarContainer.setStepToolbar(params.stepToolbar);
            }
            this.stepNavigatorAndToolbarContainer.setStepNavigator(this.stepNavigator);
            headerAndNavigatorContainer.appendChild(this.stepNavigatorAndToolbarContainer);

            this.stepsPanel = new WizardStepsPanel(this.stepNavigator, this.formPanel);
            this.stepNavigatorAndToolbarContainer.onShown((event: api.dom.ElementShownEvent) => {
                // set scroll offset equal to the height of the step navigator to switch steps at the bottom of it when sticky
                this.stepsPanel.setScrollOffset(event.getElement().getEl().getHeight());
            });
            ResponsiveManager.onAvailableSizeChanged(this.stepNavigatorAndToolbarContainer, (item: ResponsiveItem) => {
                // update offset if step navigator is resized
                if (this.isVisible()) {
                    this.updateStickyToolbar();
                    this.stepsPanel.setScrollOffset(item.getElement().getEl().getHeight());

                    this.stepNavigatorAndToolbarContainer.checkAndMinimize();
                }
            });
            this.formPanel.appendChildren(headerAndNavigatorContainer, this.stepsPanel);

            this.layingOutNew = this.persistedItem == null;

            if (this.layingOutNew) {
                this.preLayoutNew().
                    then(() => this.layoutNew()).
                    then(() => {
                        this.postLayoutNew();
                        onSuccess();
                    }).
                    catch((reason: any) => {
                        if (onError) {
                            onError(reason);
                        } else {
                            api.DefaultErrorHandler.handle(reason);
                        }
                    }).
                    done(() => this.validityManager.notifyValidityChanged(this.isValid()));
            } else {
                this.startLayoutPersistedItem(this.persistedItem).
                    then(() => {
                        this.postLayoutPersisted(this.persistedItem);
                        onSuccess();
                    }).
                    catch((reason: any) => {
                        if (onError) {
                            onError(reason);
                        } else {
                            api.DefaultErrorHandler.handle(reason);
                        }
                    }).
                    done(() => this.validityManager.notifyValidityChanged(this.isValid()));
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

        protected getWizardStepsPanel(): WizardStepsPanel {
            return this.stepsPanel;
        }

        getMainToolbar(): api.ui.toolbar.Toolbar {
            return this.mainToolbar;
        }

        updateStickyToolbar() {
            var scrollTop = this.formPanel.getHTMLElement().scrollTop;
            var wizardHeaderHeight = this.header.getEl().getHeightWithMargin();
            var navigationWidth;
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

            if (this.minimized) {
                navigationWidth = this.splitPanel.getEl().getHeight();
            } else {
                navigationWidth = this.stepsPanel.getEl().getWidth() - this.stepNavigatorAndToolbarContainer.getEl().getPaddingLeft();
            }
            this.stepNavigatorAndToolbarContainer.getEl().setWidthPx(navigationWidth);
        }

        toggleMinimize(navigationIndex: number = -1) {

            this.stepsPanel.setListenToScroll(false);

            var scroll = this.stepsPanel.getScroll();
            this.minimized = !this.minimized;

            this.stepNavigator.unNavigationItemActivated(this.toggleMinimizeListener);
            this.formPanel.toggleClass("minimized");

            if (this.minimized) {
                this.stepNavigator.setScrollEnabled(false);

                this.scrollPosition = scroll;
                this.splitPanel.savePanelSizesAndDistribute(40, 0, api.ui.panel.SplitPanelUnit.PIXEL);
                this.splitPanel.hideSplitter();
                this.minimizeEditButton.getEl().setLeftPx(this.stepsPanel.getEl().getWidth());

                this.stepNavigator.onNavigationItemActivated(this.toggleMinimizeListener);
            } else {
                this.splitPanel.loadPanelSizesAndDistribute();
                this.splitPanel.showSplitter();
                this.stepsPanel.setScroll(this.scrollPosition);

                this.stepsPanel.setListenToScroll(true);
                this.stepNavigator.setScrollEnabled(true);

                this.stepNavigator.selectNavigationItem(navigationIndex, false, true);
            }
        }

        isMinimized(): boolean {
            return this.minimized;
        }

        giveInitialFocus() {
            this.header.giveFocus();

            this.startRememberFocus();
        }

        startRememberFocus() {
            this.steps.forEach((step) => {
                step.getStepForm().onFocus((el) => {
                    this.lastFocusedElement = <HTMLElement>el.target;
                })
            })
        }

        getTabId(): api.app.bar.AppBarTabId {
            return this.tabId;
        }

        setTabId(tabId: api.app.bar.AppBarTabId) {
            this.tabId = tabId;
        }

        getHeader(): WizardHeader {
            return this.header;
        }

        getIconUrl(): string {
            return null; // TODO:
        }

        getActions(): api.ui.Action[] {
            return this.actions.getActions();
        }

        getSteps(): WizardStep[] {
            return this.steps;
        }

        setSteps(steps: WizardStep[]) {
            steps.forEach((step: WizardStep, index: number) => {
                this.addStep(step, index === 0);
            });
            this.steps = steps;
        }

        addStep(step: WizardStep, select: boolean) {
            this.stepsPanel.addNavigablePanel(step.getTabBarItem(), step.getStepForm(), step.getTabBarItem().getLabel(), select);
            this.validityManager.addItem(step);
        }

        insertStepBefore(stepToInsert: WizardStep, beforeStep: WizardStep) {
            var indexOfBeforeStep = this.steps.indexOf(beforeStep);
            this.steps.splice(indexOfBeforeStep, 0, stepToInsert);
            this.stepsPanel.insertNavigablePanel(stepToInsert.getTabBarItem(), stepToInsert.getStepForm(),
                stepToInsert.getTabBarItem().getLabel(), indexOfBeforeStep);
            this.validityManager.addItem(stepToInsert);
        }

        removeStepWithForm(form: WizardStepForm) {
            this.steps = this.steps.filter((step: WizardStep) => {
                var remove = (step.getStepForm() == form);
                if (remove) {
                    this.validityManager.removeItem(step);
                }
                return !remove;
            });
            return this.stepsPanel.removeNavigablePanel(form);
        }

        preLayoutNew(): wemQ.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of layoutNew
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        isLayingOutNew(): boolean {
            return this.layingOutNew;
        }

        layoutNew(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();
            this.actions.enableActionsForNew();
            deferred.resolve(null);
            return deferred.promise;
        }

        postLayoutNew(): wemQ.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of layoutNew
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        private startLayoutPersistedItem(persistedItem: EQUITABLE): wemQ.Promise<void> {

            this.persistedItem = persistedItem;
            this.actions.enableActionsForExisting(persistedItem);

            return this.layoutPersistedItem(persistedItem);
        }

        layoutPersistedItem(persistedItem: EQUITABLE): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        postLayoutPersisted(existing: EQUITABLE): wemQ.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of startLayoutPersistedItem
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        getPersistedItem(): EQUITABLE {
            return this.persistedItem;
        }

        isItemPersisted(): boolean {
            return this.persistedItem != null;
        }

        protected setPersistedItem(newPersistedItem: EQUITABLE): void {
            this.persistedItem = newPersistedItem;
        }

        /*
         * Override this method in specific wizard to do proper check.
         */
        hasUnsavedChanges(): boolean {
            return this.isChanged;
        }

        askUserForSaveChangesBeforeClosing() {
            new SaveBeforeCloseDialog(this).open();
        }

        saveChanges(): wemQ.Promise<EQUITABLE> {

            if (this.isItemPersisted()) {
                return this.updatePersistedItem().then((persistedItem: EQUITABLE) => {

                    this.isChanged = false;
                    return this.startLayoutPersistedItem(persistedItem).
                        then(() => persistedItem);
                });
            } else {
                return this.persistNewItem().then((persistedItem: EQUITABLE) => {

                    this.isChanged = false;
                    return this.postPersistNewItem(persistedItem).
                        then(() => this.startLayoutPersistedItem(persistedItem)).
                        then(() => persistedItem);
                });
            }
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(): wemQ.Promise<EQUITABLE> {
            throw new Error("Must be overriden by inheritor");
        }

        postPersistNewItem(persistedItem: EQUITABLE): wemQ.Promise<void> {
            // To be overridden by inheritors - if extra work is needed at end of persistNewItem
            var deferred = wemQ.defer<void>();
            deferred.resolve(null);
            return deferred.promise;
        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(): wemQ.Promise<EQUITABLE> {
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
            } else {
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

        showMinimizeEditButton() {
            this.minimizeEditButton.show();
        }

        hideMinimizeEditButton() {
            this.minimizeEditButton.hide();
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

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityManager.onValidityChanged(listener);
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.validityManager.onValidityChanged(listener);
        }

        notifyValidityChanged(valid: boolean) {
            this.validityManager.notifyValidityChanged(valid);
        }

        isValid() {
            return this.validityManager.isAllValid();
        }
    }
}