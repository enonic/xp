(function () {
    // Class definition (constructor function)
    var shader = AdminLiveEdit.ui.Shader = function () {
        this.create();
        this.registerSubscribers();
    };

    // Inherits ui.Base
    shader.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    shader.constructor = shader;

    // Shorthand ref to the prototype
    var p = shader.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/ui/componentselector/on-select', function ($component) {
            self.showAt.call(self, $component);
        });

        $liveedit.subscribe('/ui/componentselector/on-deselect', function ($component) {
            self.hide.call(self);
        });

        $liveedit.subscribe('/ui/dragdrop/on-sortstart', function ($component) {
            self.hide.call(self);
        });
    };


    p.create = function () {
        var self = this;
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-north"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-east"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-south"/>');
        $liveedit('body').append('<div class="live-edit-shader" id="live-edit-shader-west"/>');
        $liveedit('.live-edit-shader').click(function (event) {
            event.stopPropagation();
            $liveedit.publish('/ui/componentselector/on-deselect');
        });
    };


    p.showAt = function ($component) {
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
    };

}());