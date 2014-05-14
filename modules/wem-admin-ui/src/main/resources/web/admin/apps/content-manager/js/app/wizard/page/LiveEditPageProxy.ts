module app.wizard.page {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import Descriptor = api.content.page.Descriptor;
    import RegionPath = api.content.page.RegionPath;
    import PageComponentType = api.content.page.PageComponentType;
    import ComponentPath = api.content.page.ComponentPath;
    import UploadDialog = api.content.inputtype.image.UploadDialog;
    import RenderingMode = api.rendering.RenderingMode;

    import NewPageComponentIdMapEvent = api.liveedit.NewPageComponentIdMapEvent;
    import ImageOpenUploadDialogEvent = api.liveedit.ImageOpenUploadDialogEvent;
    import ImageUploadedEvent = api.liveedit.ImageUploadedEvent;
    import ImageSetEvent = api.liveedit.ImageSetEvent;
    import DraggableStartEvent = api.liveedit.DraggableStartEvent;
    import DraggableStopEvent = api.liveedit.DraggableStopEvent;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import SortableStopEvent = api.liveedit.SortableStopEvent;
    import SortableUpdateEvent = api.liveedit.SortableUpdateEvent;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ComponentSelectEvent = api.liveedit.ComponentSelectEvent;

    export interface LiveEditPageConfig {

        liveFormPanel: LiveFormPanel;

        siteTemplate: SiteTemplate;
    }

    export class LiveEditPageProxy {

        private baseUrl: string;

        private liveFormPanel: LiveFormPanel;

        private siteTemplate: SiteTemplate;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.LoadMask;

        private liveEditWindow: any;

        private liveEditJQuery: JQueryStatic;

        private dragMask: api.ui.DragMask;

        private loadedListeners: {(): void;}[] = [];

        private draggableStartListeners: {(event: DraggableStartEvent): void;}[] = [];

        private draggableStopListeners: {(event: DraggableStopEvent): void;}[] = [];

        private sortableStartListeners: {(event: SortableStartEvent): void;}[] = [];

        private sortableUpdateListeners: {(event: SortableUpdateEvent): void;}[] = [];

        private sortableStopListeners: {(event: SortableStopEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectEvent): void;}[] = [];

        private pageComponentSelectedListeners: {(event: PageComponentSelectedEvent): void;}[] = [];

        private deselectListeners: {(event: DeselectEvent): void;}[] = [];

        private pageComponentAddedListeners: {(event: PageComponentAddedEvent): void;}[] = [];

        private imageComponentSetImageListeners: {(event: ImageSetEvent): void;}[] = [];

        private pageComponentSetDescriptorListeners: {(event: PageComponentSetDescriptorEvent): void;}[] = [];

        private pageComponentRemovedListeners: {(event: PageComponentRemovedEvent): void;}[] = [];

        private pageComponentResetListeners: {(event: PageComponentResetEvent): void;}[] = [];

        private pageComponentDuplicatedListeners: {(event: PageComponentDuplicatedEvent): void;}[] = [];

        constructor(config: LiveEditPageConfig) {

            this.baseUrl = api.util.getUri("portal/edit/");
            this.liveFormPanel = config.liveFormPanel;
            this.siteTemplate = config.siteTemplate;

            this.liveEditIFrame = new api.dom.IFrameEl("live-edit-frame");
            this.loadMask = new api.ui.LoadMask(this.liveEditIFrame);
            this.dragMask = new api.ui.DragMask(this.liveEditIFrame);

            ShowContentFormEvent.on(() => {
                if (this.loadMask.isVisible()) {
                    this.loadMask.hide();
                }
            });
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

        public getLoadMask(): api.ui.LoadMask {
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

        public load(content: Content): Q.Promise<void> {

            var url = this.baseUrl + content.getContentId().toString();

            this.loadMask.show();
            this.liveEditIFrame.setSrc(url);

            var deferred = Q.defer<void>();
            this.liveEditIFrame.onLoaded((event: UIEvent) => {

                var liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];
                if (liveEditWindow && liveEditWindow.$liveEdit && typeof(liveEditWindow.initializeLiveEdit) === "function") {
                    // Give loaded page same CONFIG.baseUri as in admin
                    liveEditWindow.CONFIG = { baseUri: CONFIG.baseUri };
                    liveEditWindow.siteTemplate = this.siteTemplate;
                    liveEditWindow.content = content;

                    this.loadMask.hide();

                    this.liveEditWindow = liveEditWindow;
                    this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

                    this.listenToPage();

                    liveEditWindow.initializeLiveEdit();
                    this.notifyLoaded();
                }

                deferred.resolve(null);
            });

            return deferred.promise;
        }

        public loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element, content: api.content.Content) {

            api.util.assertNotNull(componentPath, "componentPath cannot be null");
            api.util.assertNotNull(componentPlaceholder, "componentPlaceholder cannot be null");
            api.util.assertNotNull(content, "content cannot be null");

            $.ajax({
                url: api.rendering.UriHelper.getComponentUri(content.getContentId().toString(), componentPath.toString(),
                    RenderingMode.EDIT),
                method: 'GET',
                success: (data) => {
                    var newElement = $(data);
                    $(componentPlaceholder.getHTMLElement()).replaceWith(newElement);
                    componentPlaceholder.remove();

                    this.liveEditWindow.LiveEdit.component.Selection.deselect();

                    this.liveEditWindow.LiveEdit.PlaceholderCreator.renderEmptyRegionPlaceholders();

                    var comp = this.liveEditWindow.getComponentByPath(componentPath);
                    this.liveEditWindow.LiveEdit.component.Selection.handleSelect(comp.getHTMLElement(), null, true);

                    this.liveEditJQuery(this.liveEditWindow).trigger("componentLoaded.liveEdit", [comp]);
                }
            });
        }

        public getComponentByPath(path: api.content.page.ComponentPath): any {

            return this.liveEditWindow.getComponentByPath(path.toString());
        }

        public selectComponent(path: api.content.page.ComponentPath): void {
            var comp = this.getComponentByPath(path);
            var element: HTMLElement = comp.getHTMLElement();
            this.liveEditJQuery(element).trigger('selectComponent.liveEdit', [comp, null]);
        }

        public listenToPage() {

            NewPageComponentIdMapEvent.on((event: NewPageComponentIdMapEvent) => {
                console.log('PageComponentIdMap: %o', event.getMap());
            }, this.liveEditWindow);

            ImageOpenUploadDialogEvent.on(() => {
                var uploadDialog = new UploadDialog();
                uploadDialog.onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                    new ImageUploadedEvent(event.getUploadedItem()).fire(this.liveEditWindow);
                    uploadDialog.close();
                    uploadDialog.remove();
                });
                uploadDialog.open();
            }, this.liveEditWindow);

            DraggableStartEvent.on((event: DraggableStartEvent) => {
                this.notifyDraggableStart(event);
            }, this.liveEditWindow);

            DraggableStopEvent.on((event: DraggableStopEvent) => {
                this.notifyDraggableStop(event);
            }, this.liveEditWindow);

            SortableStartEvent.on((event: SortableStartEvent) => {
                this.notifySortableStart(event);
            }, this.liveEditWindow);

            SortableStopEvent.on((event: SortableStopEvent) => {
                this.notifySortableStop(event);
            }, this.liveEditWindow);

            SortableUpdateEvent.on((event: SortableUpdateEvent) => {
                if (event.getComponent()) {
                    this.notifySortableUpdate(event);
                }
            }, this.liveEditWindow);

            PageSelectEvent.on((event: PageSelectEvent) => {
                this.notifyPageSelected(event);
            }, this.liveEditWindow);

            RegionSelectEvent.on((event: RegionSelectEvent) => {
                this.notifyRegionSelected(event);
            }, this.liveEditWindow);

            ComponentSelectEvent.un();
            ComponentSelectEvent.on((event: ComponentSelectEvent) => {
                if (event.getPathAsString()) {
                    var componentPath = ComponentPath.fromString(event.getPathAsString());
                    this.notifyPageComponentSelected(componentPath, event.getComponent().isEmpty());
                }
            }, this.liveEditWindow);

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {

                this.notifyDeselect();
            });

            this.liveEditJQuery(this.liveEditWindow).off('componentAdded.liveEdit');
            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?, regionPathAsString?: string, precedingComponentPathAsString?: string) => {

                    var componentType = PageComponentType.byShortName(componentEl.getComponentType().getName());
                    var region = RegionPath.fromString(regionPathAsString);

                    var preceedingComponent: ComponentPath = null;
                    if (precedingComponentPathAsString) {
                        preceedingComponent = ComponentPath.fromString(precedingComponentPathAsString);
                    }

                    this.notifyPageComponentAdded(componentType, region, preceedingComponent, componentEl);
                });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event, component?) => {

                var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());
                this.notifyPageComponentRemoved(componentPath);
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentReset.liveEdit', (event, component?) => {

                var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());
                this.notifyPageComponentReset(componentPath);
            });

            this.liveEditJQuery(this.liveEditWindow).off('componentDuplicated.liveEdit');
            this.liveEditJQuery(this.liveEditWindow).on('componentDuplicated.liveEdit', (event, component?, placeholder?) => {

                var componentType = PageComponentType.byShortName(component.getComponentType().getName());
                var region: RegionPath = RegionPath.fromString(component.getRegionName());
                var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());

                this.notifyPageComponentDuplicated(placeholder, componentType, region, componentPath);
            });

            ImageSetEvent.on((event: ImageSetEvent) => {
                if (!event.getErrorMessage()) {
                    this.notifyImageComponentSetImage(event);
                } else {
                    api.notify.showError(event.getErrorMessage());
                }
            }, this.liveEditWindow);


            this.liveEditJQuery(this.liveEditWindow).on('pageComponentSetDescriptor.liveEdit',
                (event, descriptor?: Descriptor, componentPathAsString?: string, componentPlaceholder?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    this.notifyPageComponentSetDescriptor(componentPath, descriptor, componentPlaceholder);
                });
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

        onDraggableStart(listener: {(event: DraggableStartEvent): void;}) {
            this.draggableStartListeners.push(listener);
        }

        unDraggableStart(listener: {(event: DraggableStartEvent): void;}) {
            this.draggableStartListeners = this.draggableStartListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggableStart(event: DraggableStartEvent) {
            this.draggableStartListeners.forEach((listener) => listener(event));
        }

        onDraggableStop(listener: {(event: DraggableStopEvent): void;}) {
            this.draggableStopListeners.push(listener);
        }

        unDraggableStop(listener: {(event: DraggableStopEvent): void;}) {
            this.draggableStopListeners = this.draggableStopListeners.filter((curr) => (curr != listener));
        }

        private notifyDraggableStop(event: DraggableStopEvent) {
            this.draggableStopListeners.forEach((listener) => listener(event));
        }

        onSortableStart(listener: (event: SortableStartEvent) => void) {
            this.sortableStartListeners.push(listener);
        }

        unSortableStart(listener: (event: SortableStartEvent) => void) {
            this.sortableStartListeners = this.sortableStartListeners.filter((curr) => (curr != listener));
        }

        private notifySortableStart(event: SortableStartEvent) {
            this.sortableStartListeners.forEach((listener) => listener(event));
        }

        onSortableUpdate(listener: {(event: SortableUpdateEvent): void;}) {
            this.sortableUpdateListeners.push(listener);
        }

        unSortableUpdate(listener: {(event: SortableUpdateEvent): void;}) {
            this.sortableUpdateListeners = this.sortableUpdateListeners.filter((curr) => (curr != listener));
        }

        private notifySortableUpdate(event: SortableUpdateEvent) {
            this.sortableUpdateListeners.forEach((listener) => listener(event));
        }

        onSortableStop(listener: {(event: SortableStopEvent): void;}) {
            this.sortableStopListeners.push(listener);
        }

        unSortableStop(listener: {(event: SortableStopEvent): void;}) {
            this.sortableStopListeners = this.sortableStopListeners.filter((curr) => (curr != listener));
        }

        private notifySortableStop(event: SortableStopEvent) {
            this.sortableStopListeners.forEach((listener) => listener(event));
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

        onPageComponentSelected(listener: {(event: PageComponentSelectedEvent): void;}) {
            this.pageComponentSelectedListeners.push(listener);
        }

        unPageComponentSelected(listener: {(event: PageComponentSelectedEvent): void;}) {
            this.pageComponentSelectedListeners = this.pageComponentSelectedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentSelected(path: ComponentPath, isEmpty: boolean) {
            var event = new PageComponentSelectedEvent(path, isEmpty);
            this.pageComponentSelectedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onDeselect(listener: {(event: DeselectEvent): void;}) {
            this.deselectListeners.push(listener);
        }

        unDeselect(listener: {(event: DeselectEvent): void;}) {
            this.deselectListeners = this.deselectListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyDeselect() {
            var event = new DeselectEvent();
            this.deselectListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageComponentAdded(listener: {(event: PageComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners.push(listener);
        }

        unPageComponentAdded(listener: {(event: PageComponentAddedEvent): void;}) {
            this.pageComponentAddedListeners = this.pageComponentAddedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentAdded(type: PageComponentType, region: RegionPath, precedingComponent: ComponentPath,
                                         element: api.dom.Element) {
            var event = new PageComponentAddedEvent(element, type, region, precedingComponent);
            this.pageComponentAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onImageComponentSetImage(listener: {(event: ImageSetEvent): void;}) {
            this.imageComponentSetImageListeners.push(listener);
        }

        unImageComponentSetImage(listener: {(event: ImageSetEvent): void;}) {
            this.imageComponentSetImageListeners = this.imageComponentSetImageListeners.filter((curr) => (curr != listener));
        }

        private notifyImageComponentSetImage(event: ImageSetEvent) {
            this.imageComponentSetImageListeners.forEach((listener) => listener(event));
        }

        onPageComponentSetDescriptor(listener: {(event: PageComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners.push(listener);
        }

        unPageComponentSetDescriptor(listener: {(event: PageComponentSetDescriptorEvent): void;}) {
            this.pageComponentSetDescriptorListeners = this.pageComponentSetDescriptorListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentSetDescriptor(path: ComponentPath, descriptor: Descriptor, componentPlaceholder: api.dom.Element) {
            var event = new PageComponentSetDescriptorEvent(path, descriptor, componentPlaceholder);
            this.pageComponentSetDescriptorListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageComponentReset(listener: {(event: PageComponentResetEvent): void;}) {
            this.pageComponentResetListeners.push(listener);
        }

        unPageComponentReset(listener: {(event: PageComponentResetEvent): void;}) {
            this.pageComponentResetListeners = this.pageComponentResetListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentReset(path: ComponentPath) {
            var event = new PageComponentResetEvent(path);
            this.pageComponentResetListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageComponentRemoved(listener: {(event: PageComponentRemovedEvent): void;}) {
            this.pageComponentRemovedListeners.push(listener);
        }

        unPageComponentRemoved(listener: {(event: PageComponentRemovedEvent): void;}) {
            this.pageComponentRemovedListeners = this.pageComponentRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentRemoved(path: ComponentPath) {
            var event = new PageComponentRemovedEvent(path);
            this.pageComponentRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageComponentDuplicated(listener: {(event: PageComponentDuplicatedEvent): void;}) {
            this.pageComponentDuplicatedListeners.push(listener);
        }

        unPageComponentDuplicated(listener: {(event: PageComponentDuplicatedEvent): void;}) {
            this.pageComponentDuplicatedListeners = this.pageComponentDuplicatedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageComponentDuplicated(placeholder: api.dom.Element, type: PageComponentType, region: RegionPath,
                                              path: ComponentPath) {
            var event = new PageComponentDuplicatedEvent(placeholder, type, region, path);
            this.pageComponentDuplicatedListeners.forEach((listener) => {
                listener(event);
            });
        }

    }
}