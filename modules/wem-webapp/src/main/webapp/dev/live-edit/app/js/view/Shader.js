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
        $(window).on('component:paragraph:edit', $.proxy(this.show, this));
        $(window).on('component:click:deselect', $.proxy(this.hide, this));
        $(window).on('component:remove', $.proxy(this.hide, this));
        $(window).on('component:sort:start', $.proxy(this.hide, this));
    };


    proto.addView = function () {
        var $body = $('body');
        $body.append('<div class="live-edit-shader" id="live-edit-shader-page"/>');

        $body.append('<div id="live-edit-shader-north" class="live-edit-shader"/>');
        $body.append('<div id="live-edit-shader-east" class="live-edit-shader"/>');
        $body.append('<div id="live-edit-shader-south" class="live-edit-shader"/>');
        $body.append('<div id="live-edit-shader-west" class="live-edit-shader"/>');

        $('.live-edit-shader').click(function (event) {
            event.stopPropagation();
            $(window).trigger('component:click:deselect');
            $(window).trigger('shader:click');
        });
    };


    proto.show = function (event, $selectedComponent) {
        var me = this;

        me.hide();

        if (util.getComponentType($selectedComponent) === 'page') {
            me.showForPage($selectedComponent);
        } else {
            me.showForComponent($selectedComponent);
        }
    };


    proto.showForPage = function ($component) {
        $('#live-edit-shader-page').css({
            top: 0,
            right: 0,
            bottom: 0,
            left: 0
        }).show();
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

        $('#live-edit-shader-page').hide();
    };

}($liveedit));