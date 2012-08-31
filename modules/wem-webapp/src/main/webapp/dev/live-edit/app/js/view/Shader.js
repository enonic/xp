(function () {
    // Class definition (constructor function)
    var shader = AdminLiveEdit.view.Shader = function () {
        this.create();
        this.bindEvents();
    };

    // Inherits ui.Base
    shader.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    shader.constructor = shader;

    // Shorthand ref to the prototype
    var p = shader.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.bindEvents = function () {
        $liveedit(window).on('component:select', $liveedit.proxy(this.show, this));

        $liveedit(window).on('component:deselect', $liveedit.proxy(this.hide, this));

        $liveedit(window).on('component:drag:start', $liveedit.proxy(this.hide, this));
    };


    p.create = function () {
        var self = this;
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-north"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-east"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-south"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-west"/>');

        $liveedit('.live-edit-shader').click(function (event) {
            event.stopPropagation();
            $liveedit(window).trigger('component:deselect');
        });
    };


    p.show = function (event, $component) {
        var self = this;

        self.hide();
        var componentInfo = util.getComponentInfo($component);
        if (componentInfo.type === 'page' && componentInfo.tagName === 'body') {
            self.showForPageBody();
        } else {
            self.showForComponent($component);
        }
    };


    // TODO: Should not be here. Need some code restructuring
    p.showForPageBody = function () {
        var $regions = $liveedit('[data-live-edit-type=region]'),
            $component,
            $componentShader,
            $componentHighlighter,
            componentBoxModel;

        $regions.each(function (i) {
            $component = $liveedit(this);
            componentBoxModel = util.getBoxModel($component);
            $componentShader = $liveedit('<div/>');
            $componentShader.addClass('live-edit-shader live-edit-shader-page');
            $componentShader.css({
                top: componentBoxModel.top,
                left: componentBoxModel.left,
                width: componentBoxModel.width,
                height: componentBoxModel.height
            });
            $liveedit('body').append($componentShader);
        });
        this.addWindowBorders();
    };


    // TODO: Should not be here. Need some code restructuring
    p.addWindowBorders = function () {
        var $windows = $liveedit('[data-live-edit-type=window]'),
            $component,
            $componentHighlighter,
            componentBoxModel;

        $windows.each(function (i) {
            $component = $liveedit(this);
            componentBoxModel = util.getBoxModel($component);

            var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlighter live-edit-highlighter-page" style="top:-5000px;left:-5000px">' +
                       '    <rect width="150" height="150"/>' +
                       '</svg>';
            $componentHighlighter = $liveedit(html);
            $componentHighlighter.css({
                top: componentBoxModel.top,
                left: componentBoxModel.left,
                width: componentBoxModel.width,
                height: componentBoxModel.height
            });
            var $highlighterRect = $componentHighlighter.find('rect');
            $highlighterRect[0].setAttribute('width', componentBoxModel.width);
            $highlighterRect[0].setAttribute('height', componentBoxModel.height);
            $componentHighlighter.css('stroke', '#141414');

            $liveedit('body').append($componentHighlighter);

        });
    };


    p.showForComponent = function ($component) {
        var documentSize = util.getDocumentSize(),
            docWidth = documentSize.width,
            docHeight = documentSize.height;

        var north = $liveedit('#live-edit-shader-north'),
            east = $liveedit('#live-edit-shader-east'),
            south = $liveedit('#live-edit-shader-south'),
            west = $liveedit('#live-edit-shader-west');

        var boxModel = util.getBoxModel($component),
            x = boxModel.left,
            y = boxModel.top,
            w = boxModel.width,
            h = boxModel.height;

        north.css({
            top: 0,
            left: 0,
            width: docWidth,
            height: y
        });

        east.css({
            top: y,
            left: x + w,
            width: docWidth - (x + w),
            height: h
        });

        south.css({
            top: y + h,
            left: 0,
            width: docWidth,
            height: docHeight - (y + h)
        });

        west.css({
            top: y,
            left: 0,
            width: x,
            height: h
        });
    };


    p.hide = function () {
        $liveedit('.live-edit-shader').css({
            top: '-15000px',
            left: '-15000px'
        });
        $liveedit('.live-edit-shader-page').remove();
        $liveedit('.live-edit-highlighter-page').remove();

    };

}());