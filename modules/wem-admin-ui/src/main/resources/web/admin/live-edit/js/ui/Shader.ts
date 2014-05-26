module LiveEdit.ui {

    import ItemView = api.liveedit.ItemView;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import PageComponentSelectComponentEvent = api.liveedit.PageComponentSelectComponentEvent;

    export class Shader extends LiveEdit.ui.Base {

        private selectedComponent: ItemView = null;

        private CLS_NAME:string = 'live-edit-shader';

        private pageShader:JQuery;
        private northShader:JQuery;
        private eastShader:JQuery;
        private southShader:JQuery;
        private westShader:JQuery;

        constructor() {
            super();
            this.addView();
            this.addEvents();
            this.registerGlobalListeners();
        }

        registerGlobalListeners():void {
            PageComponentSelectComponentEvent.on((event: PageComponentSelectComponentEvent) => this.show(event.getItemView()));
            wemjq(window).on('editTextComponent.liveEdit', (event:JQueryEventObject, component?) => this.show(component));
            PageComponentDeselectEvent.on(() => this.hide());
            PageComponentRemoveEvent.on(() => this.hide());
            SortableStartEvent.on(() => this.hide());
            wemjq(window).on('resizeBrowserWindow.liveEdit', () => this.onWindowResize());
        }

        private addView():void {
            var body:JQuery = wemjq('body');
            var clsName = this.CLS_NAME;

            this.pageShader = body.append('<div id="live-edit-page-shader" class="' + clsName + '"><!-- --></div>');

            this.northShader = wemjq('<div id="live-edit-shader-north" class="' + clsName + '"><!-- --></div>');
            body.append(this.northShader);

            this.eastShader = wemjq('<div id="live-edit-shader-east" class="' + clsName + '"><!-- --></div>');
            body.append(this.eastShader);

            this.southShader = wemjq('<div id="live-edit-shader-south" class="' + clsName + '"><!-- --></div>');
            body.append(this.southShader);

            this.westShader = wemjq('<div id="live-edit-shader-west" class="' + clsName + '"><!-- --></div>');
            body.append(this.westShader);
        }

        private addEvents():void {
            wemjq('.' + this.CLS_NAME).on('click contextmenu', (event) => {
                event.stopPropagation();
                event.preventDefault();

                this.selectedComponent.deselect();

                wemjq(window).trigger('clickShader.liveEdit');
            });
        }

        private show(component: ItemView): void {
            this.selectedComponent = component;
            if (component.getType().equals(api.liveedit.PageItemType.get())) {
                this.showForPage();
            } else {
                this.showForComponent(component);
            }
        }

        private showForPage():void {

            //this.hide();

            wemjq('#live-edit-page-shader').css({
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }).show();
        }

        private showForComponent(component: ItemView): void {
            var documentSize = LiveEdit.DomHelper.getDocumentSize(),
                documentWidth = documentSize.width,
                documentHeight = documentSize.height;

            var dimensions = component.getElementDimensions(),
                x = dimensions.left,
                y = dimensions.top,
                w = dimensions.width,
                h = dimensions.height;

            this.northShader.css({
                top: 0,
                left: 0,
                width: documentWidth,
                height: y
            }).show();

            this.eastShader.css({
                top: y,
                left: x + w,
                width: documentWidth - (x + w),
                height: h
            }).show();

            this.southShader.css({
                top: y + h,
                left: 0,
                width: documentWidth,
                height: documentHeight - (y + h)
            }).show();

            this.westShader.css({
                top: y,
                left: 0,
                width: x,
                height: h
            }).show();
        }

        private hide():void {
            this.selectedComponent = null;
            var shaders:JQuery = wemjq('.live-edit-shader');
            shaders.hide(null);
        }

        private onWindowResize():void {
            if (this.selectedComponent) {
                this.show(this.selectedComponent)
            }
        }

    }
}