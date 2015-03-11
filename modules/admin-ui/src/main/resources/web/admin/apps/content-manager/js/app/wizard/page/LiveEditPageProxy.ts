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
    import PageTextModeStartedEvent = api.liveedit.PageTextModeStartedEvent;
    import RegionSelectedEvent = api.liveedit.RegionSelectedEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemovedEvent = api.liveedit.ComponentRemovedEvent;
    import ComponentDuplicatedEvent = api.liveedit.ComponentDuplicatedEvent;
    import ComponentLoadedEvent = api.liveedit.ComponentLoadedEvent;
    import RepeatNextItemViewIdProducer = api.liveedit.RepeatNextItemViewIdProducer;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;
    import RegionView = api.liveedit.RegionView;


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

        private pageTextModeStartedListeners: {(event: PageTextModeStartedEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectedEvent): void;}[] = [];

        private itemViewSelectedListeners: {(event: ItemViewSelectedEvent): void;}[] = [];

        private itemViewDeselectedListeners: {(event: ItemViewDeselectEvent): void;}[] = [];

        private componentAddedListeners: {(event: ComponentAddedEvent): void;}[] = [];

        private componentRemovedListeners: {(event: ComponentRemovedEvent): void;}[] = [];

        private componentDuplicatedListeners: {(event: ComponentDuplicatedEvent): void;}[] = [];

        private liveEditPageViewReadyListeners: {(event: LiveEditPageViewReadyEvent): void;}[] = [];

        private LIVE_EDIT_ERROR_PAGE_BODY_ID = "wem-error-page";

        constructor() {

            this.liveEditIFrame = new api.dom.IFrameEl("live-edit-frame");
            console.log("LiveEditPageProxy.constructor ");
            this.liveEditIFrame.onLoaded(() => this.handleIFrameLoadedEvent());
            this.loadMask = new api.ui.mask.LoadMask(this.liveEditIFrame);
            this.dragMask = new api.ui.mask.DragMask(this.liveEditIFrame);

            ShowContentFormEvent.on(() => {
                if (this.loadMask.isVisible()) {
                    this.loadMask.hide();
                }
            });
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
            this.dragMask.remove();
            this.loadMask.remove();
        }

        public load() {
            this.loadMask.show();
            var contentId = this.liveEditModel.getContent().getContentId().toString();
            var pageUrl = api.rendering.UriHelper.getPortalUri(contentId, RenderingMode.EDIT, Workspace.DRAFT);
            console.log("LiveEditPageProxy.load pageUrl: " + pageUrl);
            this.liveEditIFrame.setSrc(pageUrl);
        }

        private handleIFrameLoadedEvent() {

            var liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];
            if (liveEditWindow && liveEditWindow.wemjq) {
                // Give loaded page same CONFIG.baseUri as in admin
                liveEditWindow.CONFIG = {baseUri: CONFIG.baseUri};

                this.livejq = <JQueryStatic>liveEditWindow.wemjq;
                if (this.liveEditIFrame != liveEditWindow) {
                    this.liveEditWindow = liveEditWindow;
                    this.listenToPage();
                }

                this.loadMask.hide();

                new api.liveedit.InitializeLiveEditEvent(this.liveEditModel).fire(this.liveEditWindow);


            }
            else if (liveEditWindow.document.body.id == this.LIVE_EDIT_ERROR_PAGE_BODY_ID) {
                this.loadMask.hide();
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
                    var repeatNextItemViewIdProducer = new RepeatNextItemViewIdProducer(componentView.getItemId(),
                        componentView.getItemViewIdProducer());

                    var createViewConfig = new CreateItemViewConfig<RegionView,Component>().
                        setItemViewProducer(repeatNextItemViewIdProducer).
                        setParentView(componentView.getParentItemView()).
                        setData(componentView.getComponent()).
                        setElement(newElement);
                    var newComponentView: ComponentView<Component> = componentView.getType().
                        createView(createViewConfig);

                    componentView.replaceWith(newComponentView);

                    new ComponentLoadedEvent(newComponentView).fire(this.liveEditWindow);

                    newComponentView.select();

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

        public listenToPage() {

            ImageOpenUploadDialogEvent.on((openDialogEvent: ImageOpenUploadDialogEvent) => {
                var imageUploadDialog = new ImageUploadDialog(this.liveEditModel.getContent().getContentId());
                imageUploadDialog.onImageUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                    new ImageUploadedEvent(event.getUploadItem().getModel(),
                        openDialogEvent.getTargetImagePlaceholder()).
                        fire(this.liveEditWindow);

                    imageUploadDialog.close();
                    imageUploadDialog.remove();
                });
                imageUploadDialog.open();
            }, this.liveEditWindow);

            ComponentViewDragStartedEvent.on(this.notifyComponentViewDragStarted.bind(this), this.liveEditWindow);

            ComponentViewDragStoppedEvent.on(this.notifyComponentViewDragStopped.bind(this), this.liveEditWindow);

            ComponentViewDragCanceledEvent.on(this.notifyComponentViewDragCanceled.bind(this), this.liveEditWindow);

            ComponentViewDragDroppedEvent.on(this.notifyComponentViewDragDropped.bind(this), this.liveEditWindow);

            PageSelectedEvent.on(this.notifyPageSelected.bind(this), this.liveEditWindow);

            PageLockedEvent.on(this.notifyPageLocked.bind(this), this.liveEditWindow);

            PageUnlockedEvent.on(this.notifyPageUnlocked.bind(this), this.liveEditWindow);

            PageTextModeStartedEvent.on(this.notifyPageTextModeStarted.bind(this), this.liveEditWindow);

            RegionSelectedEvent.on(this.notifyRegionSelected.bind(this), this.liveEditWindow);

            ItemViewSelectedEvent.on(this.notifyItemViewSelected.bind(this), this.liveEditWindow);

            ItemViewDeselectEvent.on(this.notifyItemViewDeselected.bind(this), this.liveEditWindow);

            ComponentAddedEvent.on(this.notifyComponentAdded.bind(this), this.liveEditWindow);

            ComponentRemovedEvent.on(this.notifyComponentRemoved.bind(this), this.liveEditWindow);

            ComponentDuplicatedEvent.on(this.notifyComponentDuplicated.bind(this), this.liveEditWindow);

            LiveEditPageViewReadyEvent.on(this.notifyLiveEditPageViewReady.bind(this), this.liveEditWindow);

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

        onItemViewDeselected(listener: {(event: ItemViewDeselectEvent): void;}) {
            this.itemViewDeselectedListeners.push(listener);
        }

        unItemViewDeselected(listener: {(event: ItemViewDeselectEvent): void;}) {
            this.itemViewDeselectedListeners = this.itemViewDeselectedListeners.filter((curr) => (curr != listener));
        }

        private notifyItemViewDeselected(event: ItemViewDeselectEvent) {
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

        onLiveEditPageViewReady(listener: {(event: LiveEditPageViewReadyEvent): void;}) {
            this.liveEditPageViewReadyListeners.push(listener);
        }

        unLiveEditPageViewReady(listener: {(event: LiveEditPageViewReadyEvent): void;}) {
            this.liveEditPageViewReadyListeners = this.liveEditPageViewReadyListeners.filter((curr) => (curr != listener));
        }

        private notifyLiveEditPageViewReady(event: LiveEditPageViewReadyEvent) {
            this.liveEditPageViewReadyListeners.forEach((listener) => listener(event));
        }

    }
}