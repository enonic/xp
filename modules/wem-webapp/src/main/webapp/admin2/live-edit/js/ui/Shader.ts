module LiveEdit.ui {
    var $ = $liveedit;

    var componentHelper = LiveEdit.ComponentHelper;

    export class Shader extends LiveEdit.ui.Base {
        private selectedComponent:JQuery = null;
        private $pageShader:JQuery;
        private $northShader:JQuery;
        private $eastShader:JQuery;
        private $southShader:JQuery;
        private $westShader:JQuery;

        constructor() {
            super();
            this.addView();
            this.addEvents();
            this.registerGlobalListeners();

            console.log('Shader instantiated. Using jQuery ' + $().jquery);
        }


        registerGlobalListeners():void {
            $(window).on('select.liveEdit.component paragraphEdit.liveEdit.component', (event:JQueryEventObject, component:JQuery) => this.show(component));

            $(window).on('deselect.liveEdit.component remove.liveEdit.component sortStart.liveEdit.component', () => this.hide());

            $(window).on('resize.liveEdit.window', () => this.handleWindowResize());
        }


        addView():void {
            var $body = $('body');

            this.$pageShader = $body.append('<div class="live-edit-shader" id="live-edit-page-shader"><!-- --></div>');

            this.$northShader = $('<div id="live-edit-shader-north" class="live-edit-shader"><!-- --></div>');
            $body.append(this.$northShader);

            this.$eastShader = $('<div id="live-edit-shader-east" class="live-edit-shader"><!-- --></div>');
            $body.append(this.$eastShader);

            this.$southShader = $('<div id="live-edit-shader-south" class="live-edit-shader"><!-- --></div>');
            $body.append(this.$southShader);

            this.$westShader = $('<div id="live-edit-shader-west" class="live-edit-shader"><!-- --></div>');
            $body.append(this.$westShader);
        }


        addEvents():void {
            $('.live-edit-shader').on('click contextmenu', function (event) {
                event.stopPropagation();
                event.preventDefault();
                $(window).trigger('deselect.liveEdit.component');
                $(window).trigger('click.liveEdit.shader');
            });
        }


        show(component:JQuery):void {
            this.selectedComponent = component;
            if (componentHelper.getComponentType(component) === 'page') {
                this.showForPage();
            } else {
                this.showForComponent(component);
            }
        }


        showForPage():void {
            this.hide();

            $('#live-edit-page-shader').css({
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }).show();
        }


        showForComponent(component:JQuery):void {
            var documentSize = LiveEdit.DomHelper.getDocumentSize(),
                docWidth = documentSize.width,
                docHeight = documentSize.height;

            var boxModel = componentHelper.getBoxModel(component),
                x = boxModel.left,
                y = boxModel.top,
                w = boxModel.width,
                h = boxModel.height;

            this.$northShader.css({
                top: 0,
                left: 0,
                width: docWidth,
                height: y
            }).show();

            this.$eastShader.css({
                top: y,
                left: x + w,
                width: docWidth - (x + w),
                height: h
            }).show();

            this.$southShader.css({
                top: y + h,
                left: 0,
                width: docWidth,
                height: docHeight - (y + h)
            }).show();

            this.$westShader.css({
                top: y,
                left: 0,
                width: x,
                height: h
            }).show();
        }


        hide():void {
            this.selectedComponent = null;
            var $shaders = $('.live-edit-shader');
            $shaders.hide();
        }


        handleWindowResize():void {
            if (this.selectedComponent) {
                this.show(this.selectedComponent)
            }
        }

    }
}