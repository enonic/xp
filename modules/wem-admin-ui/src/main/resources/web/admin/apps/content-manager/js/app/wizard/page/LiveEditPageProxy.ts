module app.wizard.page {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageModel = api.content.page.PageModel;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Component = api.content.page.Component;
    import ImageUploadDialog = api.content.form.inputtype.image.ImageUploadDialog;

    import ComponentView = api.liveedit.ComponentView;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageComponentSetImageEvent = api.liveedit.image.ImageComponentSetImageEvent;
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import DraggingComponentViewCanceledEvent = api.liveedit.DraggingComponentViewCanceledEvent;
    import ItemFromContextWindowDroppedEvent = api.liveedit.ItemFromContextWindowDroppedEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentAddedEvent = api.liveedit.ComponentAddedEvent;
    import ComponentRemoveEvent = api.liveedit.ComponentRemoveEvent;
    import ComponentResetEvent = api.liveedit.ComponentResetEvent;
    import ComponentDuplicateEvent = api.liveedit.ComponentDuplicateEvent;
    import ComponentSetDescriptorEvent = api.liveedit.ComponentSetDescriptorEvent;
    import ComponentLoadedEvent = api.liveedit.ComponentLoadedEvent;
    import RepeatNextItemViewIdProducer = api.liveedit.RepeatNextItemViewIdProducer;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;
    import RegionView = api.liveedit.RegionView;

    export interface LiveEditPageProxyConfig {

        liveFormPanel: LiveFormPanel;
    }

    export class LiveEditPageProxy {

        private baseUrl: string;

        private liveEditModel: LiveEditModel;

        private liveFormPanel: LiveFormPanel;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.mask.LoadMask;

        private liveEditWindow: any;

        private liveEditJQuery: JQueryStatic;

        private dragMask: api.ui.mask.DragMask;

        private loadedListeners: {(): void;}[] = [];

        private draggingPageComponentViewStartedListeners: {(event: DraggingComponentViewStartedEvent): void;}[] = [];

        private draggingPageComponentViewCompletedListeners: {(event: DraggingComponentViewCompletedEvent): void;}[] = [];

        private draggingPageComponentViewCanceledListeners: {(event: DraggingComponentViewCanceledEvent): void;}[] = [];

        private itemFromContextWindowDroppedListeners: {(event: ItemFromContextWindowDroppedEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectEvent): void;}[] = [];

        private itemViewSelectedListeners: {(event: ItemViewSelectedEvent): void;}[] = [];

        private deselectListeners: {(event: ItemViewDeselectEvent): void;}[] = [];

        private pageComponentAddedListeners: {(event: ComponentAddedEvent): void;}[] = [];

        private imageComponentSetImageListeners: {(event: ImageComponentSetImageEvent): void;}[] = [];

        private pageComponentSetDescriptorListeners: {(event: ComponentSetDescriptorEvent): void;}[] = [];

        private pageComponentRemovedListeners: {(event: ComponentRemoveEvent): void;}[] = [];

        private pageComponentResetListeners: {(event: ComponentResetEvent): void;}[] = [];

        private pageComponentDuplicatedListeners: {(event: ComponentDuplicateEvent): void;}[] = [];

        private LIVE_EDIT_ERROR_PAGE_BODY_ID = "wem-error-page";

        constructor(config: LiveEditPageProxyConfig) {

            this.baseUrl = api.util.UriHelper.getUri("portal/edit/stage/");
            this.liveFormPanel = config.liveFormPanel;

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

        public load() {

            this.loadMask.show();
            var pageUrl = this.baseUrl + this.liveEditModel.getContent().getContentId().toString();
            console.log("LiveEditPageProxy.load pageUrl: " + pageUrl);
            this.liveEditIFrame.setSrc(pageUrl);
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

                new api.liveedit.InitializeLiveEditEvent(this.liveEditModel).fire(this.liveEditWindow);


            }
            else if (liveEditWindow.document.body.id == this.LIVE_EDIT_ERROR_PAGE_BODY_ID) {
                this.loadMask.hide();
            }

            // Notify loaded no matter the result
            this.notifyLoaded();
        }

        public loadComponent(pageComponentView: ComponentView<Component>, componentUrl: string): wemQ.Promise<string> {

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

                    var createViewConfig = new CreateItemViewConfig<RegionView,Component>().
                        setItemViewProducer(repeatNextItemViewIdProducer).
                        setParentView(pageComponentView.getParentItemView()).
                        setData(pageComponentView.getComponent()).
                        setElement(newElement);
                    var newPageComponentView: ComponentView<Component> = pageComponentView.getType().
                        createView(createViewConfig);

                    pageComponentView.replaceWith(newPageComponentView);

                    new ComponentLoadedEvent(newPageComponentView).fire(this.liveEditWindow);

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
                var imageUploadDialog = new ImageUploadDialog(this.liveEditModel.getContent().getPath());
                imageUploadDialog.onImageUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                    new ImageUploadedEvent(event.getUploadItem().getModel(),
                        openDialogEvent.getTargetImagePlaceholder()).
                        fire(this.liveEditWindow);

                    imageUploadDialog.close();
                    imageUploadDialog.remove();
                });
                imageUploadDialog.open();
            }, this.liveEditWindow);

            DraggingComponentViewStartedEvent.on(this.notifyDraggingPageComponentViewStarted.bind(this), this.liveEditWindow);

            DraggingComponentViewCompletedEvent.on(this.notifyDraggingPageComponentViewCompleted.bind(this), this.liveEditWindow);

            DraggingComponentViewCanceledEvent.on(this.notifyDraggingPageComponentViewCanceled.bind(this), this.liveEditWindow);

            ItemFromContextWindowDroppedEvent.on(this.notifyItemFromContextWindowDropped.bind(this), this.liveEditWindow);

            PageSelectEvent.on(this.notifyPageSelected.bind(this), this.liveEditWindow);

            RegionSelectEvent.on(this.notifyRegionSelected.bind(this), this.liveEditWindow);

            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                this.notifyPageComponentSelected(event);
            }, this.liveEditWindow);

            ItemViewDeselectEvent.on(this.notifyDeselect.bind(this), this.liveEditWindow);

            ComponentAddedEvent.on(this.notifyPageComponentAdded.bind(this), this.liveEditWindow);

            ComponentRemoveEvent.on(this.notifyPageComponentRemoved.bind(this), this.liveEditWindow);

            ComponentResetEvent.on(this.notifyPageComponentReset.bind(this), this.liveEditWindow);

            ComponentDuplicateEvent.on(this.notifyPageComponentDuplicated.bind(this), this.liveEditWindow);

            ImageComponentSetImageEvent.on((event: ImageComponentSetImageEvent) => {
                if (!event.getErrorMessage()) {
                    this.notifyImageComponentSetImage(event);
                } else {
                    api.notify.showError(event.getErrorMessage());
                }
            }, this.liveEditWindow);

            ComponentSetDescriptorEvent.on(this.notifyPageComponentSetDescriptor.bind(this), this.liveEditWindow);
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

        onDraggingPageComponentViewStartedEvent(listener: (event: DraggingComponentViewStartedEvent) => void) {
            this.draggingPageComponentViewStartedListeners.push(listener);
        }

        unDraggingPageComponentViewStartedEvent(listener: (event: DraggingComponentViewStartedEvent) => void) {
            this.draggingPageComponentViewStartedListeners =
            this.draggingPageComponentViewStartedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewStarted(event: DraggingComponentViewStartedEvent) {
            this.draggingPageComponentViewStartedListeners.forEach((listener) => listener(event));
        }

        onDraggingPageComponentViewCompleted(listener: {(event: DraggingComponentViewCompletedEvent): void;}) {
            this.draggingPageComponentViewCompletedListeners.push(listener);
        }

        unDraggingPageComponentViewCompleted(listener: {(event: DraggingComponentViewCompletedEvent): void;}) {
            this.draggingPageComponentViewCompletedListeners =
            this.draggingPageComponentViewCompletedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewCompleted(event: DraggingComponentViewCompletedEvent) {
            this.draggingPageComponentViewCompletedListeners.forEach((listener) => listener(event));
        }

        onDraggingPageComponentViewCanceled(listener: {(event: DraggingComponentViewCanceledEvent): void;}) {
            this.draggingPageComponentViewCanceledListeners.push(listener);
        }

        unDraggingPageComponentViewCanceled(listener: {(event: DraggingComponentViewCanceledEvent): void;}) {
            this.draggingPageComponentViewCanceledListeners =
            this.draggingPageComponentViewCanceledListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingPageComponentViewCanceled(event: DraggingComponentViewCanceledEvent) {
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

        onPageComponentAdded(listener: {(event: ComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners.push(listener);
        }

        unPageComponentAdded(listener: {(event: ComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners = this.pageComponentAddedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentAdded(event: ComponentAddedEvent) {
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

        onPageComponentSetDescriptor(listener: {(event: ComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners.push(listener);
        }

        unPageComponentSetDescriptor(listener: {(event: ComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners = this.pageComponentSetDescriptorListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentSetDescriptor(event: ComponentSetDescriptorEvent) {
            this.pageComponentSetDescriptorListeners.forEach((listener) => listener(event));
        }

        onPageComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.pageComponentResetListeners.push(listener);
        }

        unPageComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.pageComponentResetListeners = this.pageComponentResetListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentReset(event: ComponentResetEvent) {
            this.pageComponentResetListeners.forEach((listener) => listener(event));
        }

        onPageComponentRemoved(listener: {(event: ComponentRemoveEvent): void;}) {
            this.pageComponentRemovedListeners.push(listener);
        }

        unPageComponentRemoved(listener: {(event: ComponentRemoveEvent): void;}) {
            this.pageComponentRemovedListeners = this.pageComponentRemovedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentRemoved(event: ComponentRemoveEvent) {
            this.pageComponentRemovedListeners.forEach((listener) => listener(event));
        }

        onPageComponentDuplicated(listener: {(event: ComponentDuplicateEvent): void;}) {
            this.pageComponentDuplicatedListeners.push(listener);
        }

        unPageComponentDuplicated(listener: {(event: ComponentDuplicateEvent): void;}) {
            this.pageComponentDuplicatedListeners = this.pageComponentDuplicatedListeners.filter((curr) => (curr != listener));
        }

        private notifyPageComponentDuplicated(event: ComponentDuplicateEvent) {
            this.pageComponentDuplicatedListeners.forEach((listener) => listener(event));
        }
    }
}