module app.wizard.page {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import Descriptor = api.content.page.Descriptor;
    import RegionPath = api.content.page.RegionPath;
    import ComponentPath = api.content.page.ComponentPath;
    import UploadDialog = api.content.inputtype.image.UploadDialog;
    import ImageUploadedEvent = api.ui.ImageUploadedEvent;
    import RenderingMode = api.rendering.RenderingMode;

    export interface LiveEditPageConfig {

        liveFormPanel: LiveFormPanel;

        siteTemplate: SiteTemplate;
    }

    export class LiveEditPage {

        private baseUrl: string;

        private liveFormPanel: LiveFormPanel;

        private siteTemplate: SiteTemplate;

        private liveEditIFrame: api.dom.IFrameEl;

        private loadMask: api.ui.LoadMask;

        private liveEditWindow: any;

        private liveEditJQuery: JQueryStatic;

        private dragMask: api.ui.DragMask;

        private loadedListeners: {(): void;}[] = [];

        private dragableStartListeners: {(event: DragableStartEvent): void;}[] = [];

        private dragableStopListeners: {(event: DragableStopEvent): void;}[] = [];

        private sortableStartListeners: {(event: SortableStartEvent): void;}[] = [];

        private sortableUpdateListeners: {(event: SortableUpdateEvent): void;}[] = [];

        private sortableStopListeners: {(event: SortableStopEvent): void;}[] = [];

        private pageSelectedListeners: {(event: PageSelectedEvent): void;}[] = [];

        private regionSelectedListeners: {(event: RegionSelectedEvent): void;}[] = [];

        private pageComponentSelectedListeners: {(event: PageComponentSelectedEvent): void;}[] = [];

        private deselectListeners: {(event: DeselectEvent): void;}[] = [];

        private pageComponentAddedListeners: {(event: PageComponentAddedEvent): void;}[] = [];

        private imageComponentSetImageListeners: {(event: ImageComponentSetImageEvent): void;}[] = [];

        private pageComponentSetDescriptorListeners: {(event: PageComponentSetDescriptorEvent): void;}[] = [];

        private pageComponentRemovedListeners: {(event: PageComponentRemovedEvent): void;}[] = [];

        private pageComponentResetListeners: {(event: PageComponentResetEvent): void;}[] = [];

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

        public setWidth(value:string) {
            this.liveEditIFrame.getEl().setWidth(value);
        }

        public setWidthPx(value:number) {
            this.liveEditIFrame.getEl().setWidthPx(value);
        }

        public setHeight(value:string) {
            this.liveEditIFrame.getEl().setHeight(value);
        }

        public setHeightPx(value:number) {
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
                    liveEditWindow.CONFIG = {};
                    liveEditWindow.CONFIG.baseUri = CONFIG.baseUri;
                    liveEditWindow.siteTemplate = this.siteTemplate;
                    liveEditWindow.content = content;
                    liveEditWindow.onOpenImageUploadDialogRequest(() => {
                        var uploadDialog = new UploadDialog();
                        uploadDialog.onImageUploaded((event: ImageUploadedEvent) => {
                            liveEditWindow.notifyImageUploaded(event);
                            uploadDialog.close();
                            uploadDialog.remove();
                        });
                        uploadDialog.open();
                    });
                    this.loadMask.hide();

                    liveEditWindow.initializeLiveEdit();

                    this.liveEditWindow = liveEditWindow;
                    this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

                    this.listenToPage();
                    this.notifyLoaded();
                }

                deferred.resolve(null);
            });

            return deferred.promise;
        }

        public loadComponent(componentPath: ComponentPath, componentPlaceholder: api.dom.Element, content: api.content.Content) {

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

            this.liveEditJQuery(this.liveEditWindow).on('draggableStart.liveEdit', (event) => {

                this.notifyDragableStart();
            });

            this.liveEditJQuery(this.liveEditWindow).on('draggableStop.liveEdit', (event) => {

                this.notifyDragableStop();
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableStart.liveEdit', (event) => {

                this.notifySortableStart();
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableStop.liveEdit', (event, component?) => {

                if (component) {
                    var componentPath = ComponentPath.fromString(component.getComponentPath());
                    this.notifySortableStop(componentPath, component.isEmpty(), component);
                }
                else {
                    this.notifySortableStop(null, false, null);
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, component?) => {

                if (component) {
                    var componentPath = ComponentPath.fromString(component.getComponentPath());
                    var precedingComponent = ComponentPath.fromString(component.getPrecedingComponentPath());
                    var region = RegionPath.fromString(component.getRegionName());

                    this.notifySortableUpdate(componentPath, component, region, precedingComponent);
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('pageSelect.liveEdit', (event) => {


                this.notifyPageSelected();
            });

            this.liveEditJQuery(this.liveEditWindow).on('regionSelect.liveEdit', (event, regionPathAsString?: string) => {

                var regionPath = RegionPath.fromString(regionPathAsString);
                this.notifyRegionSelected(regionPath);

            });

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit', (event, pathAsString?, component?) => {

                if (pathAsString) {
                    var componentPath = ComponentPath.fromString(pathAsString);
                    this.notifyPageComponentSelected(componentPath, component.isEmpty());
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {

                this.notifyDeselect();
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?, regionPathAsString?: string, precedingComponentPathAsString?: string) => {

                    var componentType = componentEl.getComponentType().getName();
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

            var liveEditPageWindow = window;
            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, params?: {
                    imageId?: ContentId;
                    componentPathAsString?: string;
                    componentPlaceholder?: api.dom.Element;
                    imageName?: string;
                    errorMessage?: string;
                }) => {

                    if (!params.errorMessage) {
                        var componentPath = ComponentPath.fromString(params.componentPathAsString);
                        this.notifyImageComponentSetImage(componentPath, params.imageId, params.componentPlaceholder, !params.imageName ? null : params.imageName);
                    } else {
                        api.notify.showError(params.errorMessage);
                    }

                });

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

        onDragableStart(listener: {(event: DragableStartEvent): void;}) {
            this.dragableStartListeners.push(listener);
        }

        unDragableStart(listener: {(event: DragableStartEvent): void;}) {
            this.dragableStartListeners = this.dragableStartListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyDragableStart() {
            var event = new DragableStartEvent();
            this.dragableStartListeners.forEach((listener) => {
                listener(event);
            });
        }

        onDragableStop(listener: {(event: DragableStopEvent): void;}) {
            this.dragableStopListeners.push(listener);
        }

        unDragableStop(listener: {(event: DragableStopEvent): void;}) {
            this.dragableStopListeners = this.dragableStopListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyDragableStop() {
            var event = new DragableStopEvent();
            this.dragableStopListeners.forEach((listener) => {
                listener(event);
            });
        }

        onSortableStart(listener: {(event: SortableStartEvent): void;}) {
            this.sortableStartListeners.push(listener);
        }

        unSortableStart(listener: {(event: SortableStartEvent): void;}) {
            this.sortableStartListeners = this.sortableStartListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySortableStart() {
            var event = new SortableStartEvent();
            this.sortableStartListeners.forEach((listener) => {
                listener(event);
            });
        }

        onSortableUpdate(listener: {(event: SortableUpdateEvent): void;}) {
            this.sortableUpdateListeners.push(listener);
        }

        unSortableUpdate(listener: {(event: SortableUpdateEvent): void;}) {
            this.sortableUpdateListeners = this.sortableUpdateListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySortableUpdate(componentPath: ComponentPath, component: any, region: RegionPath, precedingComponent: ComponentPath) {
            var event = new SortableUpdateEvent(componentPath, component, region, precedingComponent);
            this.sortableUpdateListeners.forEach((listener) => {
                listener(event);
            });
        }

        onSortableStop(listener: {(event: SortableStopEvent): void;}) {
            this.sortableStopListeners.push(listener);
        }

        unSortableStop(listener: {(event: SortableStopEvent): void;}) {
            this.sortableStopListeners = this.sortableStopListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySortableStop(componentPath: ComponentPath, isEmpty: boolean, component: any) {
            var event = new SortableStopEvent(componentPath, isEmpty, component);
            this.sortableStopListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageSelected(listener: {(event: PageSelectedEvent): void;}) {
            this.pageSelectedListeners.push(listener);
        }

        unPageSelected(listener: {(event: PageSelectedEvent): void;}) {
            this.pageSelectedListeners = this.pageSelectedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageSelected() {
            var event = new PageSelectedEvent();
            this.pageSelectedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onRegionSelected(listener: {(event: RegionSelectedEvent): void;}) {
            this.regionSelectedListeners.push(listener);
        }

        unRegionSelected(listener: {(event: RegionSelectedEvent): void;}) {
            this.regionSelectedListeners = this.regionSelectedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyRegionSelected(path: RegionPath) {
            var event = new RegionSelectedEvent(path);
            this.regionSelectedListeners.forEach((listener) => {
                listener(event);
            });
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

        private notifyPageComponentAdded(type: string, region: RegionPath, precedingComponent: ComponentPath, element: api.dom.Element) {
            var event = new PageComponentAddedEvent(element, type, region, precedingComponent);
            this.pageComponentAddedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onImageComponentSetImage(listener: {(event: ImageComponentSetImageEvent): void;}) {
            this.imageComponentSetImageListeners.push(listener);
        }

        unImageComponentSetImage(listener: {(event: ImageComponentSetImageEvent): void;}) {
            this.imageComponentSetImageListeners = this.imageComponentSetImageListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageComponentSetImage(path: ComponentPath, image: ContentId, componentPlaceholder: api.dom.Element,
                                             imageName: string) {
            var event = new ImageComponentSetImageEvent(path, image, componentPlaceholder, imageName);
            this.imageComponentSetImageListeners.forEach((listener) => {
                listener(event);
            });
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

    }
}