module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class Shader extends LiveEdit.ui.Base {
        private selectedComponent:LiveEdit.component.Component = null;

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
            $(window).on('selectComponent.liveEdit', (event:JQueryEventObject, component) => this.show(component));
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, component) => this.show(component));
            $(window).on('deselectComponent.liveEdit', () => this.hide());
            $(window).on('componentRemoved.liveEdit', () => this.hide());
            $(window).on('sortableStart.liveEdit', () => this.hide());
            $(window).on('resizeBrowserWindow.liveEdit', () => this.onWindowResize());
        }

        private addView():void {
            var body:JQuery = $('body');
            var clsName = this.CLS_NAME;

            this.pageShader = body.append('<div id="live-edit-page-shader" class="' + clsName + '"><!-- --></div>');

            this.northShader = $('<div id="live-edit-shader-north" class="' + clsName + '"><!-- --></div>');
            body.append(this.northShader);

            this.eastShader = $('<div id="live-edit-shader-east" class="' + clsName + '"><!-- --></div>');
            body.append(this.eastShader);

            this.southShader = $('<div id="live-edit-shader-south" class="' + clsName + '"><!-- --></div>');
            body.append(this.southShader);

            this.westShader = $('<div id="live-edit-shader-west" class="' + clsName + '"><!-- --></div>');
            body.append(this.westShader);
        }

        private addEvents():void {
            $('.' + this.CLS_NAME).on('click contextmenu', function (event) {
                event.stopPropagation();
                event.preventDefault();

                LiveEdit.component.Selection.deSelect();

                $(window).trigger('clickShader.liveEdit');
            });
        }

        private show(component:LiveEdit.component.Component):void {
            this.selectedComponent = component;
            if (component.getComponentType().getType() === LiveEdit.component.Type.PAGE) {
                this.showForPage();
            } else {
                this.showForComponent(component);
            }
        }

        private showForPage():void {
            this.hide();

            $('#live-edit-page-shader').css({
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }).show();
        }

        private showForComponent(component:LiveEdit.component.Component):void {
            var documentSize = LiveEdit.DomHelper.getDocumentSize(),
                documentWidth = documentSize.width,
                documentHeight = documentSize.height;

            var dimensions:ElementDimensions = component.getElementDimensions(),
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
            var shaders:JQuery = $('.live-edit-shader');
            shaders.hide(null);
        }

        private onWindowResize():void {
            if (this.selectedComponent) {
                this.show(this.selectedComponent)
            }
        }

    }
}