module api.app.wizard {

    import Toolbar = api.ui.toolbar.Toolbar;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import Panel = api.ui.panel.Panel;

    /*
     Only data should be passed to constructor
     views are to be created on render
     */
    export interface WizardPanelParams<EQUITABLE extends api.Equitable> {

        tabId: api.app.bar.AppBarTabId;

        persistedItem: EQUITABLE;

        actions: WizardActions<any>;

    }

    export class WizardPanel<EQUITABLE extends api.Equitable> extends api.ui.panel.Panel implements api.ui.Closeable, api.ui.ActionContainer {

        protected params: WizardPanelParams<EQUITABLE>;

        private persistedItem: EQUITABLE;

        private stepNavigator: WizardStepNavigator;

        private steps: WizardStep[];

        private stepsPanel: WizardStepsPanel;

        protected wizardHeader: WizardHeader;

        protected livePanel: Panel;

        protected mainToolbar: Toolbar;

        protected stepToolbar: Toolbar;

        protected formIcon: api.dom.Element;

        protected formMask: api.ui.mask.LoadMask;

        protected liveMask: api.ui.mask.LoadMask;

        private isChanged: boolean = true;

        private dataLoaded: boolean = false;

        protected formState: FormState = new FormState(true);

        private closedListeners: {(event: WizardClosedEvent): void}[] = [];

        private dataLoadedListeners: {(item: EQUITABLE): void}[] = [];

        protected formPanel: Panel;

        private lastFocusedElement: HTMLElement;

        private stepNavigatorAndToolbarContainer: WizardStepNavigatorAndToolbar;

        private splitPanel: api.ui.panel.SplitPanel;

        private splitPanelThreshold: number = 960;

        private stepNavigatorPlaceholder: api.dom.DivEl;

        private validityManager: WizardValidityManager;

        private minimizeEditButton: api.dom.DivEl;

        private minimized: boolean = false;

        private toggleMinimizeListener: (event: api.ui.ActivatedEvent) => void;

        private helpTextToggleButton: api.dom.DivEl;

        private helpTextShown: boolean = false;

        private scrollPosition: number = 0;

        private wizardHeaderCreatedListeners: any[] = [];

        public static debug: boolean = false;

        constructor(params: WizardPanelParams<EQUITABLE>) {
            super("wizard-panel");

            this.params = params;

            if (params.persistedItem) {
                this.setPersistedItem(params.persistedItem);
                this.formState.setIsNew(false);
            }

            // have to be in constructor because onValidityChanged uses it
            this.validityManager = new WizardValidityManager();

            // call loadData even if persistedItem is set to load additional data
            this.loadData();

            this.onRendered((event: api.dom.ElementRenderedEvent) => {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel: rendered", event);
                }
            });

            this.onShown((event: api.dom.ElementShownEvent) => {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel: shown", event);
                }
                if (this.formPanel && !this.formPanel.isRendered()) {
                    this.formMask.show();
                }
                if (this.livePanel && !this.livePanel.isRendered()) {
                    this.liveMask.show();
                }
            });

            this.onHidden((event: api.dom.ElementHiddenEvent) => {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel: hidden", event);
                }
                if (this.formMask && this.formMask.isVisible()) {
                    this.formMask.hide();
                }
                if (this.liveMask && this.liveMask.isVisible()) {
                    this.liveMask.hide();
                }
            })
        }

        /*
         Loads necessary data for rendering on wizard open
         */
        private loadData() {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.loadData");
            }

            this.dataLoaded = false;
            this.doLoadData().done((item: EQUITABLE) => {
                this.dataLoaded = true;
                if (WizardPanel.debug) {
                    console.debug("WizardPanel.loadData: data loaded", item);
                }
                this.notifyDataLoaded(item);
            }, (reason) => {
                api.DefaultErrorHandler.handle(reason);
            });
        }

        protected doLoadData(): wemQ.Promise<EQUITABLE> {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.doLoadData");
            }
            var deferred = wemQ.defer<EQUITABLE>();

            if (!this.getPersistedItem()) {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel.doLoadData: loading new data...");
                }
                // Ensure a nameless and empty content is persisted before rendering new
                this.saveChanges().then((equitable) => {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel.doLoadData: data created", equitable);
                    }
                    deferred.resolve(equitable);
                }).catch((reason) => {
                    deferred.reject(reason);
                }).done();
            } else {
                var equitable = this.getPersistedItem();
                if (WizardPanel.debug) {
                    console.debug("WizardPanel.doLoadData: data present, skipping load...", equitable);
                }
                deferred.resolve(equitable);
            }

            return deferred.promise;
        }

        protected isDataLoaded(): boolean {
            return this.dataLoaded;
        }

        /*
         Wait for loadData to finish in order to render
         */
        doRender(): wemQ.Promise<boolean> {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.doRender");
            }
            return super.doRender().then((rendered) => {

                let doRenderOnDataLoadedAndShown = () => {
                    let deferred = wemQ.defer<boolean>();
                    let doRenderOnShown = () => {
                        this.doRenderOnDataLoaded(rendered).then((rendered) => {

                            this.doLayout(this.getPersistedItem())
                                .then(() => {
                                    deferred.resolve(rendered);

                                    if (this.hasHelpText()) {
                                        this.setupHelpTextToggleButton();
                                    }
                                })
                                .catch(reason => {
                                    deferred.reject(reason);
                                    api.DefaultErrorHandler.handle(reason);
                                }).done();
                        });
                    };

                    if (this.isVisible()) {
                        doRenderOnShown();
                    } else {
                        if (WizardPanel.debug) {
                            console.debug("WizardPanel.doRender: waiting for wizard to be shown...");
                        }
                        let shownHandler = () => {
                            if (WizardPanel.debug) {
                                console.debug("WizardPanel.doRender: wizard shown, resuming render");
                            }
                            this.unShown(shownHandler);
                            doRenderOnShown();
                        };
                        this.onShown(shownHandler);
                    }
                    return deferred.promise;
                };

                if (this.isDataLoaded()) {
                    return doRenderOnDataLoadedAndShown();
                } else {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel.doRender: waiting for data to be loaded...");
                    }
                    let deferred = wemQ.defer<boolean>();

                    // ensure render happens when data loaded
                    this.onDataLoaded((item: EQUITABLE) => {
                        if (WizardPanel.debug) {
                            console.debug("WizardPanel.doRender: data loaded, resuming render");
                        }
                        doRenderOnDataLoadedAndShown()
                            .then((rendered) => deferred.resolve(rendered))
                            .catch((reason) => deferred.reject(reason));
                    });

                    return deferred.promise;
                }
            });
        }

        protected createMainToolbar(): Toolbar {
            return null;
        }

        public getMainToolbar(): Toolbar {
            return this.mainToolbar;
        }

        protected createLivePanel(): Panel {
            return null;
        }

        public getLivePanel(): Panel {
            return this.livePanel;
        }

        protected createWizardHeader(): WizardHeader {
            return null;
        }

        public getWizardHeader(): WizardHeader {
            return this.wizardHeader;
        }

        protected createFormIcon(): api.dom.Element {
            return null;
        }

        public getFormIcon(): api.dom.Element {
            return this.formIcon;
        }

        protected createStepToolbar(): Toolbar {
            return null;
        }

        public getStepToolbar(): Toolbar {
            return this.stepToolbar;
        }

        protected doRenderOnDataLoaded(rendered: boolean, delayMask?: boolean): wemQ.Promise<boolean> {
            let formMaskFn = () => {
                this.formMask = new api.ui.mask.LoadMask(this.formPanel);
                this.formMask.show();
            };
            if (WizardPanel.debug) {
                console.debug("WizardPanel.doRenderOnDataLoaded");
            }

            let updateMinimizeButtonPosition = () => {
                this.minimizeEditButton.getEl().setLeftPx(this.stepsPanel.getEl().getWidth());
            };

            this.updateToolbarActions();

            this.formPanel = new api.ui.panel.Panel("form-panel rendering");
            this.formPanel.onScroll(() => this.updateStickyToolbar());

            this.formPanel.onAdded((event) => {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel: formPanel.onAdded");
                }
                if (delayMask) {
                    setTimeout(formMaskFn, 1);
                } else {
                    formMaskFn();
                }
            });

            var firstShow;
            this.formPanel.onRendered((event) => {
                if (WizardPanel.debug) {
                    console.debug("WizardPanel: formPanel.onRendered");
                }
                firstShow = true;
                this.formMask.hide();
                this.formPanel.removeClass('rendering');

                if (this.mainToolbar) {
                    this.mainToolbar.removeClass('rendering');
                }

                if (firstShow) {
                    firstShow = false;
                    this.giveInitialFocus();
                }

                if (!!this.lastFocusedElement) {
                    this.lastFocusedElement.focus();
                }

                if (this.minimizeEditButton) {
                    updateMinimizeButtonPosition();
                }

                // check validity on rendered
                this.notifyValidityChanged(this.isValid());
            });

            this.mainToolbar = this.createMainToolbar();
            if (this.mainToolbar) {
                this.mainToolbar.addClass('rendering');
                this.appendChild(this.mainToolbar);
            }

            var headerAndNavigatorContainer = new api.dom.DivEl("header-and-navigator-container");

            this.formIcon = this.createFormIcon();
            if (this.formIcon) {
                headerAndNavigatorContainer.appendChild(this.formIcon);
            }

            this.wizardHeader = this.createWizardHeader();
            if (this.wizardHeader) {
                headerAndNavigatorContainer.appendChild(this.wizardHeader);
                this.notifyWizardHeaderCreated();
                this.validityManager.setHeader(this.wizardHeader);
            }

            this.stepNavigatorAndToolbarContainer = new WizardStepNavigatorAndToolbar("wizard-step-navigator-and-toolbar");

            this.stepToolbar = this.createStepToolbar();
            if (this.stepToolbar) {
                this.stepNavigatorAndToolbarContainer.setStepToolbar(this.stepToolbar);
            }

            this.stepNavigator = new WizardStepNavigator();
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

            this.livePanel = this.createLivePanel();
            if (this.livePanel) {
                this.livePanel.addClass('rendering');

                this.toggleMinimizeListener = (event: api.ui.ActivatedEvent) => {
                    this.toggleMinimize(event.getIndex());
                };
                this.minimizeEditButton = new api.dom.DivEl("minimize-edit icon icon-arrow-right");
                api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this.formPanel, updateMinimizeButtonPosition);

                this.minimizeEditButton.onClicked(this.toggleMinimize.bind(this, -1));

                this.formPanel.prependChild(this.minimizeEditButton);

                this.livePanel.onAdded((event) => {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel: livePanel.onAdded");
                    }
                    this.liveMask = new api.ui.mask.LoadMask(this.livePanel);
                });

                this.livePanel.onRendered((event) => {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel: livePanel.onRendered");
                    }
                    this.liveMask.hide();
                    this.livePanel.removeClass('rendering');
                });

                this.splitPanel = this.createSplitPanel(this.formPanel, this.livePanel);

                this.splitPanel.onAdded((event) => {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel: splitPanel.onAdded");
                    }
                });

                this.splitPanel.onRendered((event) => {
                    if (WizardPanel.debug) {
                        console.debug("WizardPanel: splitPanel.onRendered");
                    }
                });

                this.appendChild(this.splitPanel);

            } else {

                this.appendChild(this.formPanel);
            }

            return wemQ(rendered);
        }


        onDataLoaded(listener: (item: EQUITABLE) => void) {
            this.dataLoadedListeners.push(listener);
        }

        unDataLoaded(listener: (item: EQUITABLE) => void) {
            this.dataLoadedListeners = this.dataLoadedListeners.filter((current) => {
                return listener !== current;
            })
        }

        private notifyDataLoaded(item: EQUITABLE) {
            this.dataLoadedListeners.forEach((listener) => {
                listener(item);
            })
        }

        protected getWizardStepsPanel(): WizardStepsPanel {
            return this.stepsPanel;
        }

        updateStickyToolbar() {
            var scrollTop = this.formPanel.getHTMLElement().scrollTop;
            var wizardHeaderHeight = this.getWizardHeader().getEl().getHeightWithMargin();
            var navigationWidth;
            let mainToolbar = this.getMainToolbar();
            if (scrollTop > wizardHeaderHeight) {
                mainToolbar.removeClass("scroll-shadow");
                var stepNavigatorEl = this.stepNavigatorAndToolbarContainer.getEl().addClass("scroll-stick");
                if (!this.stepNavigatorPlaceholder) {
                    this.stepNavigatorPlaceholder = new api.dom.DivEl('toolbar-placeholder');
                    this.stepNavigatorPlaceholder.insertAfterEl(this.stepNavigatorAndToolbarContainer);
                    this.stepNavigatorPlaceholder.getEl().setWidthPx(stepNavigatorEl.getWidth()).setHeightPx(stepNavigatorEl.getHeight());
                }
            } else if (scrollTop < wizardHeaderHeight) {
                mainToolbar.addClass("scroll-shadow");
                this.stepNavigatorAndToolbarContainer.removeClass("scroll-stick");
                if (this.stepNavigatorPlaceholder) {
                    this.stepNavigatorPlaceholder.remove();
                    this.stepNavigatorPlaceholder = undefined;
                }
            }
            if (scrollTop == 0) {
                mainToolbar.removeClass("scroll-shadow");
            }

            if (this.minimized) {
                navigationWidth = this.splitPanel.getEl().getHeight();
            } else {
                navigationWidth = this.stepsPanel.getEl().getWidth() - this.stepNavigatorAndToolbarContainer.getEl().getPaddingLeft();
            }
            this.stepNavigatorAndToolbarContainer.getEl().setWidthPx(navigationWidth);
        }

        updateToolbarActions() {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.updateToolbarActions: isNew", this.formState.isNew());
            }
            if (this.formState.isNew()) {
                this.params.actions.enableActionsForNew();
            } else {
                this.params.actions.enableActionsForExisting(this.getPersistedItem());
            }
        }

        toggleMinimize(navigationIndex: number = -1) {

            this.stepsPanel.setListenToScroll(false);

            var scroll = this.stepsPanel.getScroll();
            this.minimized = !this.minimized;

            this.stepNavigator.unNavigationItemActivated(this.toggleMinimizeListener);
            this.formPanel.toggleClass("minimized");

            new MinimizeWizardPanelEvent().fire();

            if (this.minimized) {
                this.stepNavigator.setScrollEnabled(false);

                this.scrollPosition = scroll;
                this.splitPanel.savePanelSizesAndDistribute(40, 0, api.ui.panel.SplitPanelUnit.PIXEL);
                this.splitPanel.hideSplitter();
                this.minimizeEditButton.getEl().setLeftPx(this.stepsPanel.getEl().getWidth());

                if (!!this.helpTextToggleButton) {
                    this.helpTextToggleButton.hide();
                }

                this.stepNavigator.onNavigationItemActivated(this.toggleMinimizeListener);
            } else {
                this.splitPanel.loadPanelSizesAndDistribute();
                this.splitPanel.showSplitter();
                this.stepsPanel.setScroll(this.scrollPosition);

                this.stepsPanel.setListenToScroll(true);
                this.stepNavigator.setScrollEnabled(true);

                if (!!this.helpTextToggleButton) {
                    this.helpTextToggleButton.show();
                }

                this.stepNavigator.selectNavigationItem(navigationIndex, false, true);
            }
        }

        private toggleHelpTextShown() {
            this.helpTextShown = !this.helpTextShown;
            this.helpTextToggleButton.toggleClass("on", this.helpTextShown);

            this.steps.forEach((step: WizardStep) => {
                step.toggleHelpText(this.helpTextShown);
            });
        }

        hasHelpText(): boolean {
            return this.steps.some((step: WizardStep) => {
                return step.hasHelpText();
            });
        }

        private setupHelpTextToggleButton() {
            this.helpTextToggleButton = this.stepNavigatorAndToolbarContainer.setupHelpTextToggleButton();

            this.helpTextToggleButton.onClicked(() => {
                this.toggleHelpTextShown();
            });
        }
        
        isMinimized(): boolean {
            return this.minimized;
        }

        giveInitialFocus() {
            this.getWizardHeader().giveFocus();
            this.startRememberFocus();
        }

        startRememberFocus() {
            this.steps.forEach((step) => {
                step.getStepForm().onFocus((el) => {
                    this.lastFocusedElement = <HTMLElement>el.target;
                })
            })
        }

        resetLastFocusedElement() {
            this.lastFocusedElement = null;
        }

        getTabId(): api.app.bar.AppBarTabId {
            return this.params.tabId;
        }

        setTabId(tabId: api.app.bar.AppBarTabId) {
            this.params.tabId = tabId;
        }

        getIconUrl(): string {
            return null; // TODO:
        }

        getActions(): api.ui.Action[] {
            return this.params.actions.getActions();
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

        doLayout(persistedItem: EQUITABLE): wemQ.Promise<void> {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.doLayout", persistedItem);
            }
            return wemQ<void>(null);
        }

        getPersistedItem(): EQUITABLE {
            return this.persistedItem;
        }

        isItemPersisted(): boolean {
            return this.persistedItem != null;
        }

        protected setPersistedItem(newPersistedItem: EQUITABLE): void {
            if (WizardPanel.debug) {
                console.debug("WizardPanel.setPersistedItem", newPersistedItem);
            }
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
                    this.setPersistedItem(persistedItem);
                    this.isChanged = false;
                    this.formState.setIsNew(false);
                    this.updateToolbarActions();
                    return this.postUpdatePersistedItem(persistedItem);
                });

            } else {
                return this.persistNewItem().then((persistedItem: EQUITABLE) => {
                    this.setPersistedItem(persistedItem);
                    this.isChanged = false;
                    // persist new happens before render to init dummy entity and is still considered as new
                    if (this.isRendered()) {
                        this.formState.setIsNew(false);
                        this.updateToolbarActions();
                    }
                    return this.postPersistNewItem(persistedItem);
                });
            }
        }

        /*
         * Override this method in specific wizard to do actual persisting of new item.
         */
        persistNewItem(): wemQ.Promise<EQUITABLE> {
            throw new Error("Must be overriden by inheritor");
        }

        postPersistNewItem(persistedItem: EQUITABLE): wemQ.Promise<EQUITABLE> {
            // To be overridden by inheritors - if extra work is needed at end of persistNewItem

            return wemQ(persistedItem);
        }

        /*
         * Override this method in specific wizard to do actual update of item.
         */
        updatePersistedItem(): wemQ.Promise<EQUITABLE> {
            throw new Error("Must be overriden by inheritor");
        }

        postUpdatePersistedItem(persistedItem: EQUITABLE): wemQ.Promise<EQUITABLE> {
            // To be overridden by inheritors - if extra work is needed at end of updatePersistedItem

            return wemQ(persistedItem);
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
                .setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL);

            if (wemjq(window).width() > this.splitPanelThreshold) {
                splitPanel.setFirstPanelSize(38, api.ui.panel.SplitPanelUnit.PERCENT);
            }

            return splitPanel.build();
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

        onWizardHeaderCreated(listener: () => void) {
            this.validityManager.onValidityChanged(listener);
        }

        unWizardHeaderCreated(listener: () => void) {
            this.validityManager.unValidityChanged(listener);
        }

        notifyWizardHeaderCreated() {
            this.wizardHeaderCreatedListeners.forEach((listener: () => void) => {
                listener.call(this);
            });
        }

        isValid() {
            return this.validityManager.isAllValid();
        }
    }

    export class FormState {

        private formIsNew: boolean;

        constructor(isNew: boolean) {
            this.formIsNew = isNew;
        }

        setIsNew(value: boolean) {
            this.formIsNew = value;
        }

        isNew() {
            return this.formIsNew;
        }
    }
}