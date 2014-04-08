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

        private setImageComponentImageListeners: {(event: SetImageComponentImageEvent): void;}[] = [];

        private setPageComponentDescriptorListeners: {(event: SetPageComponentDescriptorEvent): void;}[] = [];

        private componentRemovedListeners: {(event: ComponentRemovedEvent): void;}[] = [];

        private componentResetListeners: {(event: ComponentResetEvent): void;}[] = [];

        constructor(config: LiveEditPageConfig) {

            this.baseUrl = api.util.getUri("portal/edit/");
            this.liveFormPanel = config.liveFormPanel;
            this.siteTemplate = config.siteTemplate;

            this.liveEditIFrame = new api.dom.IFrameEl("live-edit-frame");
            this.loadMask = new api.ui.LoadMask(this.liveEditIFrame);

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

        public removeDragMask() {
            this.dragMask.remove();
        }

        public appendDragMaskToBody() {
            api.dom.Body.get().appendChild(this.dragMask);
        }

        public load(content: Content): Q.Promise<void> {

            var deferred = Q.defer<void>();

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
                    liveEditWindow.onOpenImageUploadDialogRequest(()=> {
                        var uploadDialog = new UploadDialog();
                        uploadDialog.onImageUploaded((event: ImageUploadedEvent) => {
                            liveEditWindow.notifyImageUploaded(event);
                        });
                        uploadDialog.open();

                    })
                    this.loadMask.hide();

                    liveEditWindow.initializeLiveEdit();

                    this.liveEditWindow = liveEditWindow;
                    this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;
                    if( this.dragMask ) {
                        this.dragMask.remove();
                    }
                    this.dragMask = new api.ui.DragMask(this.liveEditIFrame);

                    this.listenToPage();
                    this.notifyLoaded();

                    deferred.resolve(null);
                }
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

        public listenToPage() {

            this.liveEditJQuery(this.liveEditWindow).on('draggableStart.liveEdit', (event) => {

                console.log("LiveEditPage: draggableStart.liveEdit");
                this.notifyDragableStart();
            });

            this.liveEditJQuery(this.liveEditWindow).on('draggableStop.liveEdit', (event) => {
                console.log("LiveEditPage: draggableStop.liveEdit");
                this.notifyDragableStop();
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableStart.liveEdit', (event) => {

                console.log("LiveEditPage: sortableStart.liveEdit");
                this.notifySortableStart();
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableStop.liveEdit', (event, component?) => {

                console.log("LiveEditPage: sortableStop.liveEdit");

                if (component) {
                    var componentPath = ComponentPath.fromString(component.getComponentPath());
                    this.notifySortableStop(componentPath, component.isEmpty());
                }
                else {
                    this.notifySortableStop(null, false);
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, component?) => {

                console.log("LiveEditPage: sortableUpdate.liveEdit");

                if (component) {
                    var componentPath = ComponentPath.fromString(component.getComponentPath());
                    var precedingComponent = ComponentPath.fromString(component.getPrecedingComponentPath());
                    var region = RegionPath.fromString(component.getRegionName());

                    this.notifySortableUpdate(componentPath, component, region, precedingComponent);
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('pageSelect.liveEdit', (event) => {

                console.log("LiveEditPage: pageSelect.liveEdit");

                this.notifyPageSelected();
            });

            this.liveEditJQuery(this.liveEditWindow).on('regionSelect.liveEdit', (event, regionPathAsString?: string) => {

                console.log("LiveEditPage: regionSelect.liveEdit");

                var regionPath = RegionPath.fromString(regionPathAsString);
                this.notifyRegionSelected(regionPath);

            });

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit', (event, pathAsString?, component?) => {

                console.log("LiveEditPage: componentSelect.liveEdit");

                if (pathAsString) {
                    var componentPath = ComponentPath.fromString(pathAsString);
                    this.notifyPageComponentSelected(componentPath, component.isEmpty());
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {

                console.log("LiveEditPage: deselectComponent.liveEdit");

                this.notifyDeselect();
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, componentEl?, regionPathAsString?: string, precedingComponentPathAsString?: string) => {

                    console.log("LiveEditPage: componentAdded.liveEdit");

                    var componentType = componentEl.getComponentType().getName();
                    var region = RegionPath.fromString(regionPathAsString);

                    var preceedingComponent: ComponentPath = null;
                    if (precedingComponentPathAsString) {
                        preceedingComponent = ComponentPath.fromString(precedingComponentPathAsString);
                    }

                    this.notifyPageComponentAdded(componentType, region, preceedingComponent, componentEl);
                });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event, component?) => {

                console.log("LiveEditPage: componentRemoved.liveEdit");

                var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());
                this.notifyComponentRemoved(componentPath);
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentReset.liveEdit', (event, component?) => {

                console.log("LiveEditPage: componentReset.liveEdit");

                var componentPath: ComponentPath = ComponentPath.fromString(component.getComponentPath());
                this.notifyComponentReset(componentPath);
            });

            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, imageId?: ContentId, componentPathAsString?: string, componentPlaceholder?: api.dom.Element,
                 imageName?: string) => {

                    console.log("LiveEditPage: imageComponentSetImage.liveEdit");

                    var componentPath = ComponentPath.fromString(componentPathAsString);

                    this.notifySetImageComponentImage(componentPath, imageId, componentPlaceholder, !imageName ? null : imageName);
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageComponentSetDescriptor.liveEdit',
                (event, descriptor?: Descriptor, componentPathAsString?: string, componentPlaceholder?) => {

                    console.log("LiveEditPage: pageComponentSetDescriptor.liveEdit");

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    this.notifySetPageComponentDescriptor(componentPath, descriptor, componentPlaceholder);
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

        private notifySortableStop(component: ComponentPath, isEmpty: boolean) {
            var event = new SortableStopEvent(component, isEmpty);
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

        onSetImageComponentImage(listener: {(event: SetImageComponentImageEvent): void;}) {
            this.setImageComponentImageListeners.push(listener);
        }

        unSetImageComponentImage(listener: {(event: SetImageComponentImageEvent): void;}) {
            this.setImageComponentImageListeners = this.setImageComponentImageListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySetImageComponentImage(path: ComponentPath, image: ContentId, componentPlaceholder: api.dom.Element,
                                             imageName: string) {
            var event = new SetImageComponentImageEvent(path, image, componentPlaceholder, imageName);
            this.setImageComponentImageListeners.forEach((listener) => {
                listener(event);
            });
        }

        onSetPageComponentDescriptor(listener: {(event: SetPageComponentDescriptorEvent): void;}) {
            this.setPageComponentDescriptorListeners.push(listener);
        }

        unSetPageComponentDescriptor(listener: {(event: SetPageComponentDescriptorEvent): void;}) {
            this.setPageComponentDescriptorListeners = this.setPageComponentDescriptorListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySetPageComponentDescriptor(path: ComponentPath, descriptor: Descriptor, componentPlaceholder: api.dom.Element) {
            var event = new SetPageComponentDescriptorEvent(path, descriptor, componentPlaceholder);
            this.setPageComponentDescriptorListeners.forEach((listener) => {
                listener(event);
            });
        }

        onComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.componentResetListeners.push(listener);
        }

        unComponentReset(listener: {(event: ComponentResetEvent): void;}) {
            this.componentResetListeners = this.componentResetListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyComponentReset(path: ComponentPath) {
            var event = new ComponentResetEvent(path);
            this.componentResetListeners.forEach((listener) => {
                listener(event);
            });
        }

        onComponentRemoved(listener: {(event: ComponentRemovedEvent): void;}) {
            this.componentRemovedListeners.push(listener);
        }

        unComponentRemoved(listener: {(event: ComponentRemovedEvent): void;}) {
            this.componentRemovedListeners = this.componentRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyComponentRemoved(path: ComponentPath) {
            var event = new ComponentRemovedEvent(path);
            this.componentRemovedListeners.forEach((listener) => {
                listener(event);
            });
        }

    }
}