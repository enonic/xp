module app.wizard.page {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageModel = api.content.page.PageModel;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Component = api.content.page.region.Component;
    import ImageUploadDialog = api.content.form.inputtype.image.ImageUploadDialog;
    import RenderingMode = api.rendering.RenderingMode;
    import Workspace = api.content.Branch;

    import ComponentView = api.liveedit.ComponentView;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import LiveEditPageViewReadyEvent = api.liveedit.LiveEditPageViewReadyEvent;
    import ComponentViewDragStartedEvent = api.liveedit.ComponentViewDragStartedEvent;
    import ComponentViewDragStoppedEvent = api.liveedit.ComponentViewDragStoppedEvent;
    import ComponentViewDragCanceledEvent = api.liveedit.ComponentViewDragCanceledEvent;
    import ComponentViewDragDroppedEvent = api.liveedit.ComponentViewDragDroppedEvent;
    import PageSelectedEvent = api.liveedit.PageSelectedEvent;
    import PageLockedEvent = api.liveedit.PageLockedEvent;
    import PageUnlockedEvent = api.liveedit.PageUnlockedEvent;
    import PageUnloadedEvent = api.liveedit.PageUnloadedEvent;
    import PageTextModeStartedEvent = api.liveedit.PageTextModeStartedEvent;
    import RegionSelectedEvent = api.liveedit.RegionSelectedEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectedEvent = api.liveedit.ItemViewDeselectedEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemovedEvent = api.liveedit.ComponentRemovedEvent;
    import ComponentDuplicatedEvent = api.liveedit.ComponentDuplicatedEvent;
    import ComponentInspectedEvent = api.liveedit.ComponentInspectedEvent;
    import PageInspectedEvent = api.liveedit.PageInspectedEvent;
    import ComponentLoadedEvent = api.liveedit.ComponentLoadedEvent;
    import ComponentResetEvent = api.liveedit.ComponentResetEvent;
    import LiveEditPageInitializationErrorEvent = api.liveedit.LiveEditPageInitializationErrorEvent;
    import ComponentFragmentCreatedEvent = api.liveedit.ComponentFragmentCreatedEvent;
    import ShowWarningLiveEditEvent = api.liveedit.ShowWarningLiveEditEvent;
    import EditContentEvent = api.content.event.EditContentEvent;
    import ItemViewIdProducer = api.liveedit.ItemViewIdProducer;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;
    import RegionView = api.liveedit.RegionView;

    import CreateHtmlAreaDialogEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;
    import LiveEditPageDialogCreatedEvent = api.liveedit.LiveEditPageDialogCreatedEvent;

    export class LiveEditPageProxy {

        private liveEditModel: LiveEditModel;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.mask.LoadMask;

        private liveEditWindow: any;

        private livejq: JQueryStatic;

        private dragMask: api.ui.mask.DragMask;

        private loadedListeners: {(): void;}[] = [];

        private componentViewDragStartedListeners: {(event: ComponentViewDragStartedEvent): void;}[] = [];

        private componentViewDragStoppedListeners: {(event: ComponentViewDragStoppedEvent): void;}[] = [];

        private componentViewDragCanceledListeners: {(event: ComponentViewDragCanceledEvent): void;}[] = [];

        private componentViewDragDroppedListeners: {(event: ComponentViewDragDroppedEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectedEvent): void;}[] = [];

        private pageLockedListeners: {(event: PageLockedEvent): void;}[] = [];

        private pageUnlockedListeners: {(event: PageUnlockedEvent): void;}[] = [];

        private pageUnloadedListeners: {(event: PageUnloadedEvent): void;}[] = [];

        private pageTextModeStartedListeners: {(event: PageTextModeStartedEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectedEvent): void;}[] = [];

        private itemViewSelectedListeners: {(event: ItemViewSelectedEvent): void;}[] = [];

        private itemViewDeselectedListeners: {(event: ItemViewDeselectedEvent): void;}[] = [];

        private componentAddedListeners: {(event: ComponentAddedEvent): void;}[] = [];

        private componentRemovedListeners: {(event: ComponentRemovedEvent): void;}[] = [];

        private componentDuplicatedListeners: {(event: ComponentDuplicatedEvent): void;}[] = [];

        private componentInspectedListeners: {(event: ComponentInspectedEvent): void;}[] = [];

        private pageInspectedListeners: {(event: PageInspectedEvent): void;}[] = [];

        private componentLoadedListeners: {(event: ComponentLoadedEvent): void;}[] = [];

        private componentResetListeners: {(event: ComponentResetEvent): void;}[] = [];

        private liveEditPageViewReadyListeners: {(event: LiveEditPageViewReadyEvent): void;}[] = [];

        private liveEditPageInitErrorListeners: {(event: LiveEditPageInitializationErrorEvent): void;}[] = [];

        private fragmentCreatedListeners: {(event: ComponentFragmentCreatedEvent): void;}[] = [];

        private showWarningListeners: {(event: ShowWarningLiveEditEvent): void;}[] = [];

        private editContentListeners: {(event: EditContentEvent): void;}[] = [];

        private createHtmlAreaDialogListeners: {(event: CreateHtmlAreaDialogEvent): void;}[] = [];

        private showLoadMaskHandler: () => void;

        private hideLoadMaskHandler: () => void;

        private liveEditPageViewReady: boolean;

        private static debug: boolean = false;

        private regionsCopyForIE;

        private controllerCopyForIE;

        constructor() {

            this.liveEditIFrame = new api.dom.IFrameEl("live-edit-frame");
            this.liveEditIFrame.onLoaded(() => this.handleIFrameLoadedEvent());
            this.loadMask = new api.ui.mask.LoadMask(this.liveEditIFrame);
            this.dragMask = new api.ui.mask.DragMask(this.liveEditIFrame);

            this.hideLoadMaskHandler = () => {
                if (this.loadMask.isVisible()) {
                    this.loadMask.hide();
                }
            };
            this.onLiveEditPageViewReady(() => {
                this.liveEditPageViewReady = true;
                this.hideLoadMaskHandler();
            });
            ShowContentFormEvent.on(this.hideLoadMaskHandler);

            this.showLoadMaskHandler = () => {
                // in case someone tries to open live edit while it's still not loaded
                if (!this.liveEditPageViewReady && this.liveEditIFrame.isVisible()) {
                    this.loadMask.show();
                }
            };

            ShowLiveEditEvent.on(this.showLoadMaskHandler);
            ShowSplitEditEvent.on(this.showLoadMaskHandler);
        }

        public setModel(liveEditModel: LiveEditModel) {
            this.liveEditModel = liveEditModel;
        }

        public setWidth(value: string) {
            this.liveEditIFrame.getEl().setWidth(value);
        }

        public setWidthPx(value: number) {
            this.liveEditIFrame.getEl().setWidthPx(value);
        }

        public setHeight(value: string) {
            this.liveEditIFrame.getEl().setHeight(value);
        }

        public setHeightPx(value: number) {
            this.liveEditIFrame.getEl().setHeightPx(value);
        }

        public getWidth(): number {
            return this.liveEditIFrame.getEl().getWidth();
        }

        public getHeight(): number {
            return this.liveEditIFrame.getEl().getHeight();
        }

        public getIFrame(): api.dom.IFrameEl {
            return this.liveEditIFrame;
        }

        public getLoadMask(): api.ui.mask.LoadMask {
            return this.loadMask;
        }

        public getJQuery(): JQueryStatic {
            return this.livejq;
        }

        public createDraggable(item: JQuery) {
            this.liveEditWindow.api.liveedit.DragAndDrop.get().createDraggable(item);
        }

        public destroyDraggable(item: JQuery) {
            this.liveEditWindow.api.liveedit.DragAndDrop.get().destroyDraggable(item);
        }

        public getDragMask(): api.ui.mask.DragMask {
            return this.dragMask;
        }

        public remove() {
            ShowLiveEditEvent.un(this.showLoadMaskHandler);
            ShowSplitEditEvent.un(this.showLoadMaskHandler);
            ShowContentFormEvent.un(this.hideLoadMaskHandler);
            LiveEditPageViewReadyEvent.un(this.hideLoadMaskHandler);
            this.dragMask.remove();
            this.loadMask.remove();
        }

        public load() {
            this.liveEditPageViewReady = false;
            this.showLoadMaskHandler();

            var contentId = this.liveEditModel.getContent().getContentId().toString();
            var pageUrl = api.rendering.UriHelper.getPortalUri(contentId, RenderingMode.EDIT, Workspace.DRAFT);
            if (LiveEditPageProxy.debug) {
                console.log("LiveEditPageProxy.load pageUrl: " + pageUrl);
            }

            if (api.BrowserHelper.isIE()) {
                this.copyObjectsBeforeFrameReloadForIE();
            }

            this.liveEditIFrame.setSrc(pageUrl);
        }

        public skipNextReloadConfirmation(skip: boolean) {
            new api.liveedit.SkipLiveEditReloadConfirmationEvent(skip).fire(this.liveEditWindow);
        }

        private handleIFrameLoadedEvent() {
            var liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];

            if (liveEditWindow) {
                if (liveEditWindow.wemjq) {
                    // Give loaded page same CONFIG.baseUri as in admin
                    liveEditWindow.CONFIG = {baseUri: CONFIG.baseUri, assetsUri: CONFIG.assetsUri};

                    this.livejq = <JQueryStatic>liveEditWindow.wemjq;

                    if (this.liveEditWindow) {
                        this.stopListening(this.liveEditWindow);
                    }

                    this.liveEditWindow = liveEditWindow;

                    this.listenToPage(this.liveEditWindow);

                    if (api.BrowserHelper.isIE()) {
                        this.resetObjectsAfterFrameReloadForIE();
                        this.disableLinksInLiveEditForIE();
                    }
                    new api.liveedit.InitializeLiveEditEvent(this.liveEditModel).fire(this.liveEditWindow);
                }
                else {
                    this.notifyLiveEditPageViewReady(new api.liveedit.LiveEditPageViewReadyEvent());
                }
            }

            // Notify loaded no matter the result
            this.notifyLoaded();
        }

        public loadComponent(componentView: ComponentView<Component>, componentUrl: string): wemQ.Promise<string> {
            var deferred = wemQ.defer<string>();
            api.util.assertNotNull(componentView, "componentView cannot be null");
            api.util.assertNotNull(componentUrl, "componentUrl cannot be null");

            wemjq.ajax({
                url: componentUrl,
                type: 'GET',
                success: (htmlAsString: string) => {
                    var newElement = api.dom.Element.fromString(htmlAsString);
                    var itemViewIdProducer = componentView.getItemViewIdProducer();

                    var createViewConfig = new CreateItemViewConfig<RegionView,Component>().
                        setItemViewProducer(itemViewIdProducer).
                        setParentView(componentView.getParentItemView()).
                        setData(componentView.getComponent()).
                        setElement(newElement);
                    var newComponentView: ComponentView<Component> = componentView.getType().
                        createView(createViewConfig);

                    componentView.replaceWith(newComponentView);

                    var event = new ComponentLoadedEvent(newComponentView, componentView);
                    event.fire(this.liveEditWindow);

                    newComponentView.select();
                    newComponentView.hideContextMenu();

                    deferred.resolve("");
                },
                error: (jqXHR: JQueryXHR, textStatus: string, errorThrow: string) => {
                    var responseHtml = wemjq.parseHTML(jqXHR.responseText);
                    var errorMessage = "";
                    responseHtml.forEach((el: HTMLElement, i) => {
                        if (el.tagName && el.tagName.toLowerCase() == "title") {
                            errorMessage = el.innerHTML;
                        }
                    });
                    deferred.reject(errorMessage);
                }
            });

            return deferred.promise;
        }

        public stopListening(contextWindow: any) {

            ImageOpenUploadDialogEvent.un(null, contextWindow);

            ComponentViewDragStartedEvent.un(null, contextWindow);

            ComponentViewDragStoppedEvent.un(null, contextWindow);

            ComponentViewDragCanceledEvent.un(null, contextWindow);

            ComponentViewDragDroppedEvent.un(null, contextWindow);

            PageSelectedEvent.un(null, contextWindow);

            PageLockedEvent.un(null, contextWindow);

            PageUnlockedEvent.un(null, contextWindow);

            PageUnloadedEvent.un(null, contextWindow);

            PageTextModeStartedEvent.un(null, contextWindow);

            RegionSelectedEvent.un(null, contextWindow);

            ItemViewSelectedEvent.un(null, contextWindow);

            ItemViewDeselectedEvent.un(null, contextWindow);

            ComponentAddedEvent.un(null, contextWindow);

            ComponentRemovedEvent.un(null, contextWindow);

            ComponentDuplicatedEvent.un(null, contextWindow);

            ComponentInspectedEvent.un(null, contextWindow);

            PageInspectedEvent.un(null, contextWindow);

            ComponentFragmentCreatedEvent.un(null, contextWindow);

            ShowWarningLiveEditEvent.un(null, contextWindow);

            ComponentLoadedEvent.un(null, contextWindow);

            ComponentResetEvent.un(null, contextWindow);

            LiveEditPageViewReadyEvent.un(null, contextWindow);

            LiveEditPageInitializationErrorEvent.un(null, contextWindow);

            CreateHtmlAreaDialogEvent.un(null, contextWindow);
        }

        public listenToPage(contextWindow: any) {

            ImageOpenUploadDialogEvent.on((openDialogEvent: ImageOpenUploadDialogEvent) => {
                var imageUploadDialog = new ImageUploadDialog(this.liveEditModel.getContent().getContentId());
                imageUploadDialog.onImageUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                    new ImageUploadedEvent(event.getUploadItem().getModel(),
                        openDialogEvent.getTargetImagePlaceholder()).fire(contextWindow);

                    imageUploadDialog.close();
                    imageUploadDialog.remove();
                });
                imageUploadDialog.open();
            }, contextWindow);

            ComponentViewDragStartedEvent.on(this.notifyComponentViewDragStarted.bind(this), contextWindow);

            ComponentViewDragStoppedEvent.on(this.notifyComponentViewDragStopped.bind(this), contextWindow);

            ComponentViewDragCanceledEvent.on(this.notifyComponentViewDragCanceled.bind(this), contextWindow);

            ComponentViewDragDroppedEvent.on(this.notifyComponentViewDragDropped.bind(this), contextWindow);

            PageSelectedEvent.on(this.notifyPageSelected.bind(this), contextWindow);

            PageLockedEvent.on(this.notifyPageLocked.bind(this), contextWindow);

            PageUnlockedEvent.on(this.notifyPageUnlocked.bind(this), contextWindow);

            PageUnloadedEvent.on(this.notifyPageUnloaded.bind(this), contextWindow);

            PageTextModeStartedEvent.on(this.notifyPageTextModeStarted.bind(this), contextWindow);

            RegionSelectedEvent.on(this.notifyRegionSelected.bind(this), contextWindow);

            ItemViewSelectedEvent.on(this.notifyItemViewSelected.bind(this), contextWindow);

            ItemViewDeselectedEvent.on(this.notifyItemViewDeselected.bind(this), contextWindow);

            ComponentAddedEvent.on(this.notifyComponentAdded.bind(this), contextWindow);

            ComponentRemovedEvent.on(this.notifyComponentRemoved.bind(this), contextWindow);

            ComponentDuplicatedEvent.on(this.notifyComponentDuplicated.bind(this), contextWindow);

            ComponentInspectedEvent.on(this.notifyComponentInspected.bind(this), contextWindow);

            PageInspectedEvent.on(this.notifyPageInspected.bind(this), contextWindow);

            ComponentFragmentCreatedEvent.on(this.notifyFragmentCreated.bind(this), contextWindow);

            ShowWarningLiveEditEvent.on(this.notifyShowWarning.bind(this), contextWindow);

            EditContentEvent.on(this.notifyEditContent.bind(this), contextWindow);

            ComponentLoadedEvent.on(this.notifyComponentLoaded.bind(this), contextWindow);

            ComponentResetEvent.on(this.notifyComponentReset.bind(this), contextWindow);

            LiveEditPageViewReadyEvent.on(this.notifyLiveEditPageViewReady.bind(this), contextWindow);

            LiveEditPageInitializationErrorEvent.on(this.notifyLiveEditPageInitializationError.bind(this), contextWindow);

            CreateHtmlAreaDialogEvent.on(this.notifyLiveEditPageDialogCreate.bind(this), contextWindow);
        }

        onLoaded(listener: {(): void;}) {
            this.loadedListeners.push(listener);
        }

        unLoaded(listener: {(): void;}) {
            this.loadedListeners = this.loadedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyLoaded() {
            this.loadedListeners.forEach((listener) => {
                listener();
            });
        }

        onComponentViewDragStarted(listener: (event: ComponentViewDragStartedEvent) => void) {
            this.componentViewDragStartedListeners.push(listener);
        }

        unComponentViewDragStarted(listener: (event: ComponentViewDragStartedEvent) => void) {
            this.componentViewDragStartedListeners = this.componentViewDragStartedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentViewDragStarted(event: ComponentViewDragStartedEvent) {
            this.componentViewDragStartedListeners.forEach((listener) => listener(event));
        }

        onComponentViewDragStopped(listener: {(event: ComponentViewDragStoppedEvent): void;}) {
            this.componentViewDragStoppedListeners.push(listener);
        }

        unComponentViewDragStopped(listener: {(event: ComponentViewDragStoppedEvent): void;}) {
            this.componentViewDragStoppedListeners = this.componentViewDragStoppedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentViewDragStopped(event: ComponentViewDragStoppedEvent) {
            this.componentViewDragStoppedListeners.forEach((listener) => listener(event));
        }

        onComponentViewDragCanceled(listener: {(event: ComponentViewDragCanceledEvent): void;}) {
            this.componentViewDragCanceledListeners.push(listener);
        }

        unComponentViewDragCanceled(listener: {(event: ComponentViewDragCanceledEvent): void;}) {
            this.componentViewDragCanceledListeners = this.componentViewDragCanceledListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentViewDragCanceled(event: ComponentViewDragCanceledEvent) {
            this.componentViewDragCanceledListeners.forEach((listener) => listener(event));
        }

        onComponentViewDragDropped(listener: {(event: ComponentViewDragDroppedEvent): void;}) {
            this.componentViewDragDroppedListeners.push(listener);
        }

        unComponentViewDragDropped(listener: {(event: ComponentViewDragDroppedEvent): void;}) {
            this.componentViewDragDroppedListeners = this.componentViewDragDroppedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentViewDragDropped(event: ComponentViewDragDroppedEvent) {
            this.componentViewDragDroppedListeners.forEach((listener) => listener(event));
        }

        onPageSelected(listener: (event: PageSelectedEvent) => void) {
            this.pageSelectedListeners.push(listener);
        }

        unPageSelected(listener: (event: PageSelectedEvent) => void) {
            this.pageSelectedListeners = this.pageSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageSelected(event: PageSelectedEvent) {
            this.pageSelectedListeners.forEach((listener) => listener(event));
        }

        onPageLocked(listener: (event: PageLockedEvent) => void) {
            this.pageLockedListeners.push(listener);
        }

        unPageLocked(listener: (event: PageLockedEvent) => void) {
            this.pageLockedListeners = this.pageLockedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageLocked(event: PageLockedEvent) {
            this.pageLockedListeners.forEach((listener) => listener(event));
        }

        onPageUnlocked(listener: (event: PageUnlockedEvent) => void) {
            this.pageUnlockedListeners.push(listener);
        }

        unPageUnlocked(listener: (event: PageUnlockedEvent) => void) {
            this.pageUnlockedListeners = this.pageUnlockedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageUnlocked(event: PageUnlockedEvent) {
            this.pageUnlockedListeners.forEach((listener) => listener(event));
        }

        onPageUnloaded(listener: (event: PageUnloadedEvent) => void) {
            this.pageUnloadedListeners.push(listener);
        }

        unPageUnloaded(listener: (event: PageUnloadedEvent) => void) {
            this.pageUnloadedListeners = this.pageUnloadedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageUnloaded(event: PageUnloadedEvent) {
            this.pageUnloadedListeners.forEach((listener) => listener(event));
        }

        onPageTextModeStarted(listener: (event: PageTextModeStartedEvent) => void) {
            this.pageTextModeStartedListeners.push(listener);
        }

        unPageTextModeStarted(listener: (event: PageTextModeStartedEvent) => void) {
            this.pageTextModeStartedListeners = this.pageTextModeStartedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageTextModeStarted(event: PageTextModeStartedEvent) {
            this.pageTextModeStartedListeners.forEach((listener) => listener(event));
        }

        onRegionSelected(listener: {(event: RegionSelectedEvent): void;}) {
            this.regionSelectedListeners.push(listener);
        }

        unRegionSelected(listener: {(event: RegionSelectedEvent): void;}) {
            this.regionSelectedListeners = this.regionSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyRegionSelected(event: RegionSelectedEvent) {
            this.regionSelectedListeners.forEach((listener) => listener(event));
        }

        onItemViewSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners.push(listener);
        }

        unItemViewSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners = this.itemViewSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyItemViewSelected(event: ItemViewSelectedEvent) {
            this.itemViewSelectedListeners.forEach((listener) => listener(event));
        }

        onItemViewDeselected(listener: {(event: ItemViewDeselectedEvent): void;}) {
            this.itemViewDeselectedListeners.push(listener);
        }

        unItemViewDeselected(listener: {(event: ItemViewDeselectedEvent): void;}) {
            this.itemViewDeselectedListeners = this.itemViewDeselectedListeners.filter((curr) => (curr != listener));
        }

        private notifyItemViewDeselected(event: ItemViewDeselectedEvent) {
            this.itemViewDeselectedListeners.forEach((listener) => listener(event));
        }

        onComponentAdded(listener: {(event: ComponentAddedEvent): void;}) {
            this.componentAddedListeners.push(listener);
        }

        unComponentAdded(listener: {(event: ComponentAddedEvent): void;}) {
            this.componentAddedListeners = this.componentAddedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentAdded(event: ComponentAddedEvent) {
            this.componentAddedListeners.forEach((listener) => listener(event));
        }

        onComponentRemoved(listener: {(event: ComponentRemovedEvent): void;}) {
            this.componentRemovedListeners.push(listener);
        }

        unComponentRemoved(listener: {(event: ComponentRemovedEvent): void;}) {
            this.componentRemovedListeners = this.componentRemovedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentRemoved(event: ComponentRemovedEvent) {
            this.componentRemovedListeners.forEach((listener) => listener(event));
        }

        onComponentDuplicated(listener: {(event: ComponentDuplicatedEvent): void;}) {
            this.componentDuplicatedListeners.push(listener);
        }

        unComponentDuplicated(listener: {(event: ComponentDuplicatedEvent): void;}) {
            this.componentDuplicatedListeners = this.componentDuplicatedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentDuplicated(event: ComponentDuplicatedEvent) {
            this.componentDuplicatedListeners.forEach((listener) => listener(event));
        }

        onComponentInspected(listener: {(event: ComponentInspectedEvent): void;}) {
            this.componentInspectedListeners.push(listener);
        }

        unComponentInspected(listener: {(event: ComponentInspectedEvent): void;}) {
            this.componentInspectedListeners = this.componentInspectedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentInspected(event: ComponentInspectedEvent) {
            this.componentInspectedListeners.forEach((listener) => listener(event));
        }

        onPageInspected(listener: {(event: PageInspectedEvent): void;}) {
            this.pageInspectedListeners.push(listener);
        }

        unPageInspected(listener: {(event: PageInspectedEvent): void;}) {
            this.pageInspectedListeners = this.pageInspectedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageInspected(event: PageInspectedEvent) {
            this.pageInspectedListeners.forEach((listener) => listener(event));
        }

        onComponentLoaded(listener: {(event: ComponentLoadedEvent): void;}) {
            this.componentLoadedListeners.push(listener);
        }

        unComponentLoaded(listener: {(event: ComponentLoadedEvent): void;}) {
            this.componentLoadedListeners = this.componentLoadedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentLoaded(event: ComponentLoadedEvent) {
            this.componentLoadedListeners.forEach((listener) => listener(event));
        }

        onComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.componentResetListeners.push(listener);
        }

        unComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.componentResetListeners = this.componentResetListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentReset(event: ComponentResetEvent) {
            this.componentResetListeners.forEach((listener) => listener(event));
        }

        onLiveEditPageViewReady(listener: {(event: LiveEditPageViewReadyEvent): void;}) {
            this.liveEditPageViewReadyListeners.push(listener);
        }

        unLiveEditPageViewReady(listener: {(event: LiveEditPageViewReadyEvent): void;}) {
            this.liveEditPageViewReadyListeners = this.liveEditPageViewReadyListeners.filter((curr) => (curr != listener));
        }

        private notifyLiveEditPageViewReady(event: LiveEditPageViewReadyEvent) {
            this.liveEditPageViewReadyListeners.forEach((listener) => listener(event));
        }

        onLiveEditPageInitializationError(listener: {(event: LiveEditPageInitializationErrorEvent): void;}) {
            this.liveEditPageInitErrorListeners.push(listener);
        }

        unLiveEditPageInitializationError(listener: {(event: LiveEditPageInitializationErrorEvent): void;}) {
            this.liveEditPageInitErrorListeners = this.liveEditPageInitErrorListeners.filter((curr) => (curr != listener));
        }

        private notifyLiveEditPageInitializationError(event: LiveEditPageInitializationErrorEvent) {
            this.liveEditPageInitErrorListeners.forEach((listener) => listener(event));
        }

        onLiveEditPageDialogCreate(listener: {(event: CreateHtmlAreaDialogEvent): void;}) {
            this.createHtmlAreaDialogListeners.push(listener);
        }

        unLiveEditPageDialogCreate(listener: {(event: CreateHtmlAreaDialogEvent): void;}) {
            this.createHtmlAreaDialogListeners = this.createHtmlAreaDialogListeners.filter((curr) => (curr != listener));
        }

        private notifyLiveEditPageDialogCreate(event: CreateHtmlAreaDialogEvent) {
            this.createHtmlAreaDialogListeners.forEach((listener) => listener(event));
        }

        notifyLiveEditPageDialogCreated(modalDialog: api.util.htmlarea.dialog.ModalDialog, config:any) {
            new LiveEditPageDialogCreatedEvent(modalDialog, config).fire(this.liveEditWindow);
        }

        onComponentFragmentCreated(listener: {(event: ComponentFragmentCreatedEvent): void;}) {
            this.fragmentCreatedListeners.push(listener);
        }

        unComponentFragmentCreated(listener: {(event: ComponentFragmentCreatedEvent): void;}) {
            this.fragmentCreatedListeners = this.fragmentCreatedListeners.filter((curr) => (curr != listener));
        }

        private notifyFragmentCreated(event: ComponentFragmentCreatedEvent) {
            this.fragmentCreatedListeners.forEach((listener) => listener(event));
        }

        onShowWarning(listener: {(event: ShowWarningLiveEditEvent): void;}) {
            this.showWarningListeners.push(listener);
        }

        unShowWarning(listener: {(event: ShowWarningLiveEditEvent): void;}) {
            this.showWarningListeners = this.showWarningListeners.filter((curr) => (curr != listener));
        }

        private notifyShowWarning(event: ShowWarningLiveEditEvent) {
            this.showWarningListeners.forEach((listener) => listener(event));
        }

        onEditContent(listener: {(event: EditContentEvent): void;}) {
            this.editContentListeners.push(listener);
        }

        unEditContent(listener: {(event: EditContentEvent): void;}) {
            this.editContentListeners = this.editContentListeners.filter((curr) => (curr != listener));
        }

        private notifyEditContent(event: EditContentEvent) {
            this.editContentListeners.forEach((listener) => listener(event));
        }

        private copyObjectsBeforeFrameReloadForIE() {
            this.copyControllerForIE();
            this.copyRegionsForIE();
        }

        private copyControllerForIE() {
            var controller = this.liveEditModel.getPageModel().getController();
            if (controller) {
                this.controllerCopyForIE = JSON.parse(JSON.stringify(controller));
                this.controllerCopyForIE.key = controller.getKey().toString();
            }
        }

        private copyRegionsForIE() {
            var regions = this.liveEditModel.getPageModel().getRegions();
            if (regions) {
                this.regionsCopyForIE = JSON.parse(JSON.stringify(regions.toJson()));
            }
        }

        private resetObjectsAfterFrameReloadForIE() {
            this.resetControllerForIE();
            this.resetRegionsForIE();
        }

        private resetControllerForIE() {
            if (this.controllerCopyForIE) {
                var controller = new api.content.page.PageDescriptorBuilder().fromJson(this.controllerCopyForIE).build();
                this.liveEditModel.getPageModel().setControllerDescriptor(controller);
            }
        }

        private resetRegionsForIE() {
            if (this.regionsCopyForIE) {
                var regions = api.content.page.region.Regions.create().fromJson(this.regionsCopyForIE, null).build();
                this.liveEditModel.getPageModel().setRegions(regions);
            }
        }

        private disableLinksInLiveEditForIE() {
            if (this.livejq) {
                this.livejq("a").attr("disabled", "disabled"); // this works only in IE
            }
        }

    }
}