module app.wizard.page {

    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import PageModel = api.content.page.PageModel;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Component = api.content.page.region.Component;
    import ImageUploadDialog = api.content.form.inputtype.image.ImageUploadDialog;
    import RenderingMode = api.rendering.RenderingMode;
    import Workspace = api.content.Workspace;

    import ComponentView = api.liveedit.ComponentView;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import LiveEditPageViewReadyEvent = api.liveedit.LiveEditPageViewReadyEvent;
    import DraggingComponentViewStartedEvent = api.liveedit.DraggingComponentViewStartedEvent;
    import DraggingComponentViewCompletedEvent = api.liveedit.DraggingComponentViewCompletedEvent;
    import DraggingComponentViewCanceledEvent = api.liveedit.DraggingComponentViewCanceledEvent;
    import ItemFromContextWindowDroppedEvent = api.liveedit.ItemFromContextWindowDroppedEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ItemViewSelectedEvent = api.liveedit.ItemViewSelectedEvent;
    import ItemViewDeselectEvent = api.liveedit.ItemViewDeselectEvent;
    import ComponentRemoveEvent = api.liveedit.ComponentRemoveEvent;
    import ComponentDuplicateEvent = api.liveedit.ComponentDuplicateEvent;
    import ComponentLoadedEvent = api.liveedit.ComponentLoadedEvent;
    import RepeatNextItemViewIdProducer = api.liveedit.RepeatNextItemViewIdProducer;
    import CreateItemViewConfig = api.liveedit.CreateItemViewConfig;
    import RegionView = api.liveedit.RegionView;


    export class LiveEditPageProxy {

        private liveEditModel: LiveEditModel;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.mask.LoadMask;

        private liveEditWindow: any;

        private liveEditJQuery: JQueryStatic;

        private dragMask: api.ui.mask.DragMask;

        private loadedListeners: {(): void;}[] = [];

        private draggingComponentViewStartedListeners: {(event: DraggingComponentViewStartedEvent): void;}[] = [];

        private draggingComponentViewCompletedListeners: {(event: DraggingComponentViewCompletedEvent): void;}[] = [];

        private draggingComponentViewCanceledListeners: {(event: DraggingComponentViewCanceledEvent): void;}[] = [];

        private itemFromContextWindowDroppedListeners: {(event: ItemFromContextWindowDroppedEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectEvent): void;}[] = [];

        private itemViewSelectedListeners: {(event: ItemViewSelectedEvent): void;}[] = [];

        private deselectListeners: {(event: ItemViewDeselectEvent): void;}[] = [];

        private componentRemovedListeners: {(event: ComponentRemoveEvent): void;}[] = [];

        private componentDuplicatedListeners: {(event: ComponentDuplicateEvent): void;}[] = [];

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
            var contentId = this.liveEditModel.getContent().getContentId().toString();
            var pageUrl = api.rendering.UriHelper.getPortalUri(contentId, RenderingMode.EDIT, Workspace.STAGE);
            console.log("LiveEditPageProxy.load pageUrl: " + pageUrl);
            this.liveEditIFrame.setSrc(pageUrl);
        }

        private handleIFrameLoadedEvent() {

            var liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];
            if (liveEditWindow && liveEditWindow.wemjq) {
                // Give loaded page same CONFIG.baseUri as in admin
                liveEditWindow.CONFIG = {baseUri: CONFIG.baseUri};

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

            DraggingComponentViewStartedEvent.on(this.notifyDraggingComponentViewStarted.bind(this), this.liveEditWindow);

            DraggingComponentViewCompletedEvent.on(this.notifyDraggingComponentViewCompleted.bind(this), this.liveEditWindow);

            DraggingComponentViewCanceledEvent.on(this.notifyDraggingComponentViewCanceled.bind(this), this.liveEditWindow);

            ItemFromContextWindowDroppedEvent.on(this.notifyItemFromContextWindowDropped.bind(this), this.liveEditWindow);

            PageSelectEvent.on(this.notifyPageSelected.bind(this), this.liveEditWindow);

            RegionSelectEvent.on(this.notifyRegionSelected.bind(this), this.liveEditWindow);

            ItemViewSelectedEvent.on((event: ItemViewSelectedEvent) => {
                this.notifyItemViewSelected(event);
            }, this.liveEditWindow);

            ItemViewDeselectEvent.on(this.notifyDeselect.bind(this), this.liveEditWindow);

            ComponentRemoveEvent.on(this.notifyComponentRemoved.bind(this), this.liveEditWindow);

            ComponentDuplicateEvent.on(this.notifyComponentDuplicated.bind(this), this.liveEditWindow);

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

        onDraggingComponentViewStartedEvent(listener: (event: DraggingComponentViewStartedEvent) => void) {
            this.draggingComponentViewStartedListeners.push(listener);
        }

        unDraggingComponentViewStartedEvent(listener: (event: DraggingComponentViewStartedEvent) => void) {
            this.draggingComponentViewStartedListeners =
            this.draggingComponentViewStartedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingComponentViewStarted(event: DraggingComponentViewStartedEvent) {
            this.draggingComponentViewStartedListeners.forEach((listener) => listener(event));
        }

        onDraggingComponentViewCompleted(listener: {(event: DraggingComponentViewCompletedEvent): void;}) {
            this.draggingComponentViewCompletedListeners.push(listener);
        }

        unDraggingComponentViewCompleted(listener: {(event: DraggingComponentViewCompletedEvent): void;}) {
            this.draggingComponentViewCompletedListeners =
            this.draggingComponentViewCompletedListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingComponentViewCompleted(event: DraggingComponentViewCompletedEvent) {
            this.draggingComponentViewCompletedListeners.forEach((listener) => listener(event));
        }

        onDraggingComponentViewCanceled(listener: {(event: DraggingComponentViewCanceledEvent): void;}) {
            this.draggingComponentViewCanceledListeners.push(listener);
        }

        unDraggingComponentViewCanceled(listener: {(event: DraggingComponentViewCanceledEvent): void;}) {
            this.draggingComponentViewCanceledListeners =
            this.draggingComponentViewCanceledListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggingComponentViewCanceled(event: DraggingComponentViewCanceledEvent) {
            this.draggingComponentViewCanceledListeners.forEach((listener) => listener(event));
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

        onItemViewSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners.push(listener);
        }

        unItemViewSelected(listener: {(event: ItemViewSelectedEvent): void;}) {
            this.itemViewSelectedListeners = this.itemViewSelectedListeners.filter((curr) => (curr != listener));
        }

        private notifyItemViewSelected(event: ItemViewSelectedEvent) {
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

        onComponentRemoved(listener: {(event: ComponentRemoveEvent): void;}) {
            this.componentRemovedListeners.push(listener);
        }

        unComponentRemoved(listener: {(event: ComponentRemoveEvent): void;}) {
            this.componentRemovedListeners = this.componentRemovedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentRemoved(event: ComponentRemoveEvent) {
            this.componentRemovedListeners.forEach((listener) => listener(event));
        }

        onComponentDuplicated(listener: {(event: ComponentDuplicateEvent): void;}) {
            this.componentDuplicatedListeners.push(listener);
        }

        unComponentDuplicated(listener: {(event: ComponentDuplicateEvent): void;}) {
            this.componentDuplicatedListeners = this.componentDuplicatedListeners.filter((curr) => (curr != listener));
        }

        private notifyComponentDuplicated(event: ComponentDuplicateEvent) {
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