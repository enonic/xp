(function ($) {
    // Class definition (constructor function)
    var shader = AdminLiveEdit.view.Shader = function () {
        this.addView();
        this.registerGlobalListeners();
    };

    // Inherits ui.Base
    shader.prototype = new AdminLiveEdit.view.Base();

    // Fix constructor as it now is Base
    shader.constructor = shader;

    // Shorthand ref to the prototype
    var proto = shader.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        $(window).on('component:select', $.proxy(this.show, this));
        $(window).on('component:deselect', $.proxy(this.hide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:drag:start', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var $body = $('body');
        $body.append('<div class="live-edit-shader" id="live-edit-shader-north"/>');
        $body.append('<div class="live-edit-shader" id="live-edit-shader-east"/>');
        $body.append('<div class="live-edit-shader" id="live-edit-shader-south"/>');
        $body.append('<div class="live-edit-shader" id="live-edit-shader-west"/>');

        $('.live-edit-shader').click(function (event) {
            event.stopPropagation();
            $(window).trigger('component:deselect');
        });
    };


    proto.show = function (event, $selectedComponent) {
        var me = this;
        me.hide();
        me.showForComponent($selectedComponent);
    };


    // TODO: Should not be here. Need some code restructuring
    proto.addWindowBorders = function () {
        var $windows = $('[data-live-edit-type=window]'),
            $component,
            $componentHighlighter,
            componentBoxModel;

        $windows.each(function (i) {
            $component = $(this);
            componentBoxModel = util.getBoxModel($component);

            var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlighter live-edit-highlighter-page" style="top:-5000px;left:-5000px">' +
                       '    <rect width="150" height="150"/>' +
                       '</svg>';
            $componentHighlighter = $(html);
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

            $('body').append($componentHighlighter);

        });
    };


    proto.showForComponent = function ($component) {
        var documentSize = util.getDocumentSize(),
            docWidth = documentSize.width,
            docHeight = documentSize.height;

        var north = $('#live-edit-shader-north'),
            east = $('#live-edit-shader-east'),
            south = $('#live-edit-shader-south'),
            west = $('#live-edit-shader-west');

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


    proto.hide = function () {
        $('.live-edit-shader').css({
            top: '-15000px',
            left: '-15000px'
        });
        $('.live-edit-shader-page').remove();
        $('.live-edit-highlighter-page').remove();

    };

}($liveedit));