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
        $(window).on('component:sort:start', $.proxy(this.hide, this));
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