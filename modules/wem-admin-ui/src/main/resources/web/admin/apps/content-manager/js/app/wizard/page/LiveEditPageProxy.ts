module app.wizard.page {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import ContentId = api.content.ContentId;
    import Descriptor = api.content.page.Descriptor;
    import PageRegions = api.content.page.PageRegions;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;
    import PageComponentType = api.content.page.PageComponentType;
    import UploadDialog = api.content.form.inputtype.image.UploadDialog;
    import RenderingMode = api.rendering.RenderingMode;

    import PageComponentView = api.liveedit.PageComponentView;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageComponentSetImageEvent = api.liveedit.image.ImageComponentSetImageEvent;
    import DraggingPageComponentViewStartedEvent = api.liveedit.DraggingPageComponentViewStartedEvent;
    import DraggingPageComponentViewCompletedEvent = api.liveedit.DraggingPageComponentViewCompletedEvent;
    import DraggingPageComponentViewCanceledEvent = api.liveedit.DraggingPageComponentViewCanceledEvent;
    import ItemFromContextWindowDroppedEvent = api.liveedit.ItemFromContextWindowDroppedEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import PageComponentAddedEvent = api.liveedit.PageComponentAddedEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;
    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;
    import PageComponentSetDescriptorEvent = api.liveedit.PageComponentSetDescriptorEvent;
    import PageComponentLoadedEvent = api.liveedit.PageComponentLoadedEvent;
    import RepeatNextItemViewIdProducer = api.liveedit.RepeatNextItemViewIdProducer;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export interface LiveEditPageProxyConfig {

        liveFormPanel: LiveFormPanel;

        site: Site;
    }

    export class LiveEditPageProxy {

        private baseUrl: string;

        private pageRegions: PageRegions;

        private liveFormPanel: LiveFormPanel;

        private site: Site;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.mask.LoadMask;

        private liveEditWindow: any;

        private liveEditJQuery: JQueryStatic;

        private dragMask: api.ui.mask.DragMask;

        private iFrameLoadDeffered: wemQ.Deferred<void>;

        private contentLoadedOnPage: Content;

        private loadedListeners: {(): void;}[] = [];

        private draggingPageComponentViewStartedListeners: {(event: DraggingPageComponentViewStartedEvent): void;}[] = [];

        private draggingPageComponentViewCompletedListeners: {(event: DraggingPageComponentViewCompletedEvent): void;}[] = [];

        private draggingPageComponentViewCanceledListeners: {(event: DraggingPageComponentViewCanceledEvent): void;}[] = [];

        private itemFromContextWindowDroppedListeners: {(event: ItemFromContextWindowDroppedEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectEvent): void;}[] = [];

        private itemViewSelectedListeners: {(event: ItemViewSelectedEvent): void;}[] = [];

        private deselectListeners: {(event: ItemViewDeselectEvent): void;}[] = [];

        private pageComponentAddedListeners: {(event: PageComponentAddedEvent): void;}[] = [];

        private imageComponentSetImageListeners: {(event: ImageComponentSetImageEvent): void;}[] = [];

        private pageComponentSetDescriptorListeners: {(event: PageComponentSetDescriptorEvent): void;}[] = [];

        private pageComponentRemovedListeners: {(event: PageComponentRemoveEvent): void;}[] = [];

        private pageComponentResetListeners: {(event: PageComponentResetEvent): void;}[] = [];

        private pageComponentDuplicatedListeners: {(event: PageComponentDuplicateEvent): void;}[] = [];

        private LIVE_EDIT_ERROR_PAGE_BODY_ID = "wem-error-page";

        constructor(config: LiveEditPageProxyConfig) {

            this.baseUrl = api.util.UriHelper.getUri("portal/edit/stage/");
            this.liveFormPanel = config.liveFormPanel;
            this.site = config.site;

            this.liveEditIFrame = new api.dom.IFrameEl("live-edit-frame");
            this.iFrameLoadDeffered = wemQ.defer<void>();
            this.liveEditIFrame.onLoaded(() => this.handleIFrameLoadedEvent());
            this.loadMask = new api.ui.mask.LoadMask(this.liveEditIFrame);
            this.dragMask = new api.ui.mask.DragMask(this.liveEditIFrame);

            ShowContentFormEvent.on(() => {
                if (this.loadMask.isVisible()) {
                    this.loadMask.hide();
                }
            });
        }

        public setPageRegions(pageRegions: PageRegions) {
            this.pageRegions = pageRegions;
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

        public getLiveEditJQuery(): JQueryStatic {
            return this.liveEditJQuery;
        }

        public createDraggable(item: JQuery) {

            this.liveEditWindow.LiveEdit.component.dragdropsort.DragDropSort.createJQueryUiDraggable(item);
        }

        public showDragMask() {
            this.dragMask.show();
        }

        public hideDragMask() {
            this.dragMask.hide();
        }

        public remove() {
            this.dragMask.remove();
            this.loadMask.remove();
        }

        public load(content: Content): wemQ.Promise<void> {

            this.iFrameLoadDeffered = wemQ.defer<void>();
            this.contentLoadedOnPage = content;

            this.loadMask.show();
            var pageUrl = this.baseUrl + content.getContentId().toString();
            console.log("LiveEditPageProxy.load pageUrl: " + pageUrl);
            this.liveEditIFrame.setSrc(pageUrl);

            return this.iFrameLoadDeffered.promise;
        }

        private handleIFrameLoadedEvent() {
            var liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];
            if (liveEditWindow && liveEditWindow.wemjq) {
                // Give loaded page same CONFIG.baseUri as in admin
                liveEditWindow.CONFIG = { baseUri: CONFIG.baseUri };

                this.liveEditJQuery = <JQueryStatic>liveEditWindow.wemjq;
                if (this.liveEditIFrame != liveEditWindow) {
                    this.liveEditWindow = liveEditWindow;
                    this.listenToPage();
                }

                this.loadMask.hide();

                new api.liveedit.InitializeLiveEditEvent(this.contentLoadedOnPage, this.site,
                    this.pageRegions).fire(this.liveEditWindow);

                this.notifyLoaded();
            } else if (liveEditWindow.document.body.id == this.LIVE_EDIT_ERROR_PAGE_BODY_ID) {
                this.loadMask.hide();
            }

            this.iFrameLoadDeffered.resolve(null);
        }

        public loadComponent(pageComponentView: PageComponentView<PageComponent>, componentUrl: string): wemQ.Promise<string> {

            var deferred = wemQ.defer<string>();
            api.util.assertNotNull(pageComponentView, "pageComponentView cannot be null");
            api.util.assertNotNull(componentUrl, "componentUrl cannot be null");

            wemjq.ajax({
                url: componentUrl,
                type: 'GET',
                success: (htmlAsString: string) => {

                    var newElement = api.dom.Element.fromString(htmlAsString);
                    var repeatNextItemViewIdProducer = new RepeatNextItemViewIdProducer(pageComponentView.getItemId(),
                        pageComponentView.getItemViewIdProducer());

                    var createViewConfig = new CreateItemViewConfig<RegionView,PageComponent>().
                        setItemViewProducer(repeatNextItemViewIdProducer).
                        setParentView(pageComponentView.getParentItemView()).
                        setData(pageComponentView.getPageComponent()).
                        setElement(newElement);
                    var newPageComponentView: PageComponentView<PageComponent> = pageComponentView.getType().
                        createView(createViewConfig);

                    pageComponentView.replaceWith(newPageComponentView);

                    new PageComponentLoadedEvent(newPageComponentView).fire(this.liveEditWindow);

                    newPageComponentView.select();

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
                var uploadDialog = new UploadDialog();
                uploadDialog.onImageUploaded((event: api.ui.uploader.ImageUploadedEvent) => {
                    new ImageUploadedEvent(event.getUploadedItem(), openDialogEvent.getTargetImagePlaceholder()).fire(this.liveEditWindow);
                    uploadDialog.close();
                    uploadDialog.remove();
                });
                uploadDialog.open();
            }, this.liveEditWindow);

            DraggingPageComponentViewStartedEvent.on(this.notifyDraggingPageComponentViewStarted.bind(this), this.liveEditWindow);

            DraggingPageComponentViewCompletedEvent.on(this.notifyDraggingPageComponentViewCompleted.bind(this), this.liveEditWindow);

            DraggingPageComponentViewCanceledEvent.on(this.notifyDraggingPageComponentViewCanceled.bind(this), this.liveEditWindow);

            ItemFromContextWindowDroppedEvent.on(this.notifyItemFromContextWindowDropped.bind(this), this.liveEditWindow);

            PageSelectEvent.on(this.notifyPageSelected.bind(this), this.liveEditWindow);

            RegionSelectEvent.on(this.notifyRegionSelected.bind(this), this.liveEditWindow);

            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                this.notifyPageComponentSelected(event);
            }, this.liveEditWindow);

            ItemViewDeselectEvent.on(this.notifyDeselect.bind(this), this.liveEditWindow);

            PageComponentAddedEvent.on(this.notifyPageComponentAdded.bind(this), this.liveEditWindow);

            PageComponentRemoveEvent.on(this.notifyPageComponentRemoved.bind(this), this.liveEditWindow);

            PageComponentResetEvent.on(this.notifyPageComponentReset.bind(this), this.liveEditWindow);

            PageComponentDuplicateEvent.on(this.notifyPageComponentDuplicated.bind(this), this.liveEditWindow);

            ImageComponentSetImageEvent.on((event: ImageComponentSetImageEvent) => {
                if (!event.getErrorMessage()) {
                    this.notifyImageComponentSetImage(event);
                } else {
                    api.notify.showError(event.getErrorMessage());
                }
            }, this.liveEditWindow);

            PageComponentSetDescriptorEvent.on(this.notifyPageComponentSetDescriptor.bind(this), this.liveEditWindow);
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

        onDraggingPageComponentViewStartedEvent(listener: (event: DraggingPageComponentViewStartedEvent) => void) {
            this.draggingPageComponentViewStartedListeners.push(listener);
        }

        unDraggingPageComponentViewStartedEvent(listener: (event: DraggingPageComponentViewStartedEvent) => void) {
            this.draggingPageComponentViewStartedListeners =
            this.draggingPageComponentViewStartedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewStarted(event: DraggingPageComponentViewStartedEvent) {
            this.draggingPageComponentViewStartedListeners.forEach((listener) => listener(event));
        }

        onDraggingPageComponentViewCompleted(listener: {(event: DraggingPageComponentViewCompletedEvent): void;}) {
            this.draggingPageComponentViewCompletedListeners.push(listener);
        }

        unDraggingPageComponentViewCompleted(listener: {(event: DraggingPageComponentViewCompletedEvent): void;}) {
            this.draggingPageComponentViewCompletedListeners =
            this.draggingPageComponentViewCompletedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewCompleted(event: DraggingPageComponentViewCompletedEvent) {
            this.draggingPageComponentViewCompletedListeners.forEach((listener) => listener(event));
        }

        onDraggingPageComponentViewCanceled(listener: {(event: DraggingPageComponentViewCanceledEvent): void;}) {
            this.draggingPageComponentViewCanceledListeners.push(listener);
        }

        unDraggingPageComponentViewCanceled(listener: {(event: DraggingPageComponentViewCanceledEvent): void;}) {
            this.draggingPageComponentViewCanceledListeners =
            this.draggingPageComponentViewCanceledListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewCanceled(event: DraggingPageComponentViewCanceledEvent) {
            this.draggingPageComponentViewCanceledListeners.forEach((listener) => listener(event));
        }

        onItemFromContextWindowDropped(listener: {(event: ItemFromContextWindowDroppedEvent): void;}) {
            this.itemFromContextWindowDroppedListeners.push(listener);
        }

        unItemFromContextWindowDropped(listener: {(event: ItemFromContextWindowDroppedEvent): void;}) {
            this.itemFromContextWindowDroppedListeners =
            this.itemFromContextWindowDroppedListeners.filter((curr) => (curr != listener));
        }

        private notifyItemFromContextWindowDropped(event: ItemFromContextWindowDroppedEvent) {
            this.itemFromContextWindowDroppedListeners.forEach((listener) => listener(event));
        }

        onPageSelected(listener: (event: PageSelectEvent) => void) {
            this.pageSelectedListeners.push(listener);
        }

        unPageSelected(listener: (event: PageSelectEvent) => void) {
            this.pageSelectedListeners = this.pageSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageSelected(event: PageSelectEvent) {
            this.pageSelectedListeners.forEach((listener) => listener(event));
        }

        onRegionSelected(listener: {(event: RegionSelectEvent): void;}) {
            this.regionSelectedListeners.push(listener);
        }

        unRegionSelected(listener: {(event: RegionSelectEvent): void;}) {
            this.regionSelectedListeners = this.regionSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyRegionSelected(event: RegionSelectEvent) {
            this.regionSelectedListeners.forEach((listener) => listener(event));
        }

        onPageComponentSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners.push(listener);
        }

        unPageComponentSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners = this.itemViewSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentSelected(event: ItemViewSelectedEvent) {
            this.itemViewSelectedListeners.forEach((listener) => listener(event));
        }

        onDeselect(listener: {(event: ItemViewDeselectEvent): void;}) {
            this.deselectListeners.push(listener);
        }

        unDeselect(listener: {(event: ItemViewDeselectEvent): void;}) {
            this.deselectListeners = this.deselectListeners.filter((curr) => (curr != listener));
        }

        private notifyDeselect(event: ItemViewDeselectEvent) {
            this.deselectListeners.forEach((listener) => listener(event));
        }

        onPageComponentAdded(listener: {(event: PageComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners.push(listener);
        }

        unPageComponentAdded(listener: {(event: PageComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners = this.pageComponentAddedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentAdded(event: PageComponentAddedEvent) {
            this.pageComponentAddedListeners.forEach((listener) => listener(event));
        }

        onImageComponentSetImage(listener: {(event: ImageComponentSetImageEvent): void;}) {
            this.imageComponentSetImageListeners.push(listener);
        }

        unImageComponentSetImage(listener: {(event: ImageComponentSetImageEvent): void;}) {
            this.imageComponentSetImageListeners = this.imageComponentSetImageListeners.filter((curr) => (curr != listener));
        }

        private notifyImageComponentSetImage(event: ImageComponentSetImageEvent) {
            this.imageComponentSetImageListeners.forEach((listener) => listener(event));
        }

        onPageComponentSetDescriptor(listener: {(event: PageComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners.push(listener);
        }

        unPageComponentSetDescriptor(listener: {(event: PageComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners = this.pageComponentSetDescriptorListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentSetDescriptor(event: PageComponentSetDescriptorEvent) {
            this.pageComponentSetDescriptorListeners.forEach((listener) => listener(event));
        }

        onPageComponentReset(listener: {(event: PageComponentResetEvent): void;}) {
            this.pageComponentResetListeners.push(listener);
        }

        unPageComponentReset(listener: {(event: PageComponentResetEvent): void;}) {
            this.pageComponentResetListeners = this.pageComponentResetListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentReset(event: PageComponentResetEvent) {
            this.pageComponentResetListeners.forEach((listener) => listener(event));
        }

        onPageComponentRemoved(listener: {(event: PageComponentRemoveEvent): void;}) {
            this.pageComponentRemovedListeners.push(listener);
        }

        unPageComponentRemoved(listener: {(event: PageComponentRemoveEvent): void;}) {
            this.pageComponentRemovedListeners = this.pageComponentRemovedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentRemoved(event: PageComponentRemoveEvent) {
            this.pageComponentRemovedListeners.forEach((listener) => listener(event));
        }

        onPageComponentDuplicated(listener: {(event: PageComponentDuplicateEvent): void;}) {
            this.pageComponentDuplicatedListeners.push(listener);
        }

        unPageComponentDuplicated(listener: {(event: PageComponentDuplicateEvent): void;}) {
            this.pageComponentDuplicatedListeners = this.pageComponentDuplicatedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentDuplicated(event: PageComponentDuplicateEvent) {
            this.pageComponentDuplicatedListeners.forEach((listener) => listener(event));
        }
    }
}