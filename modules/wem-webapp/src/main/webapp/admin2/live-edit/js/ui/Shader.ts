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


        registerGlobalListeners() {
            $(window).on('component.onSelect', $.proxy(this.show, this));
            $(window).on('component.onDeselect', $.proxy(this.hide, this));
            $(window).on('component.onRemove', $.proxy(this.hide, this));
            $(window).on('component.onSortStart', $.proxy(this.hide, this));
            $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
            $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
        }


        addView() {
            var $body = $('body');

            this.$pageShader = $body.append('<div class="live-edit-shader" id="live-edit-page-shader"/>');

            this.$northShader = $('<div id="live-edit-shader-north" class="live-edit-shader"/>');
            $body.append(this.$northShader);

            this.$eastShader = $('<div id="live-edit-shader-east" class="live-edit-shader"/>');
            $body.append(this.$eastShader);

            this.$southShader = $('<div id="live-edit-shader-south" class="live-edit-shader"/>');
            $body.append(this.$southShader);

            this.$westShader = $('<div id="live-edit-shader-west" class="live-edit-shader"/>');
            $body.append(this.$westShader);
        }


        addEvents() {
            $('.live-edit-shader').on('click contextmenu', function (event) {
                event.stopPropagation();
                event.preventDefault();
                $(window).trigger('component.onDeselect');
                $(window).trigger('shader.onClick');
            });
        }


        show(event, $component) {
            var me = this;

            me.selectedComponent = $component;

            if (componentHelper.getComponentType($component) === 'page') {
                me.showForPage($component);
            } else {
                me.showForComponent($component);
            }
        }


        showForPage($component) {
            this.hide();

            $('#live-edit-page-shader').css({
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }).show();
        }


        showForComponent($component) {
            var me = this;

            $('.live-edit-shader').addClass('live-edit-animatable');

            var documentSize = LiveEdit.DomHelper.getDocumentSize(),
                docWidth = documentSize.width,
                docHeight = documentSize.height;

            var boxModel = componentHelper.getBoxModel($component),
                x = boxModel.left,
                y = boxModel.top,
                w = boxModel.width,
                h = boxModel.height;

            me.$northShader.css({
                top: 0,
                left: 0,
                width: docWidth,
                height: y
            }).show();

            me.$eastShader.css({
                top: y,
                left: x + w,
                width: docWidth - (x + w),
                height: h
            }).show();

            me.$southShader.css({
                top: y + h,
                left: 0,
                width: docWidth,
                height: docHeight - (y + h)
            }).show();

            me.$westShader.css({
                top: y,
                left: 0,
                width: x,
                height: h
            }).show();
        }


        hide() {
            this.selectedComponent = null;
            var $shaders = $('.live-edit-shader');

            $shaders.removeClass('live-edit-animatable');

            $shaders.hide();
        }


        handleWindowResize(event) {
            if (this.selectedComponent) {
                this.show(event, this.selectedComponent)
            }
        }

    }
}
/*
 AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.view');

 (function ($) {
 // Class definition (constructor function)
 var shader = AdminLiveEdit.view.Shader = function () {
 this.$selectedComponent = null;

 this.addView();
 this.addEvents();
 this.registerGlobalListeners();
 };

 // Inherits ui.Base
 shader.prototype = new AdminLiveEdit.view.Base();

 // Fix constructor as it now is Base
 // shader.constructor = shader;

 // Shorthand ref to the prototype
 var proto = shader.prototype;

 // Uses
 var componentHelper = LiveEdit.ComponentHelper;


 // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 proto.registerGlobalListeners = function () {
 $(window).on('component.onSelect', $.proxy(this.show, this));
 $(window).on('component.onDeselect', $.proxy(this.hide, this));
 $(window).on('component.onRemove', $.proxy(this.hide, this));
 $(window).on('component.onSortStart', $.proxy(this.hide, this));
 $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
 $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
 };


 proto.addView = function () {
 var $body = $('body');

 this.$pageShader = $body.append('<div class="live-edit-shader" id="live-edit-page-shader"/>');

 this.$northShader = $('<div id="live-edit-shader-north" class="live-edit-shader"/>');
 $body.append(this.$northShader);

 this.$eastShader = $('<div id="live-edit-shader-east" class="live-edit-shader"/>');
 $body.append(this.$eastShader);

 this.$southShader = $('<div id="live-edit-shader-south" class="live-edit-shader"/>');
 $body.append(this.$southShader);

 this.$westShader = $('<div id="live-edit-shader-west" class="live-edit-shader"/>');
 $body.append(this.$westShader);
 };


 proto.addEvents = function () {
 $('.live-edit-shader').on('click contextmenu', function (event) {
 event.stopPropagation();
 event.preventDefault();
 $(window).trigger('component.onDeselect');
 $(window).trigger('shader.onClick');
 });
 };


 proto.show = function (event, $component) {
 var me = this;

 me.$selectedComponent = $component;

 if (componentHelper.getComponentType($component) === 'page') {
 me.showForPage($component);
 } else {
 me.showForComponent($component);
 }
 };


 proto.showForPage = function ($component) {
 this.hide();

 $('#live-edit-page-shader').css({
 top: 0,
 right: 0,
 bottom: 0,
 left: 0
 }).show();
 };


 proto.showForComponent = function ($component) {
 var me = this;

 $('.live-edit-shader').addClass('live-edit-animatable');

 var documentSize = LiveEdit.DomHelper.getDocumentSize(),
 docWidth = documentSize.width,
 docHeight = documentSize.height;

 var boxModel = componentHelper.getBoxModel($component),
 x = boxModel.left,
 y = boxModel.top,
 w = boxModel.width,
 h = boxModel.height;

 me.$northShader.css({
 top: 0,
 left: 0,
 width: docWidth,
 height: y
 }).show();

 me.$eastShader.css({
 top: y,
 left: x + w,
 width: docWidth - (x + w),
 height: h
 }).show();

 me.$southShader.css({
 top: y + h,
 left: 0,
 width: docWidth,
 height: docHeight - (y + h)
 }).show();

 me.$westShader.css({
 top: y,
 left: 0,
 width: x,
 height: h
 }).show();
 };


 proto.hide = function () {
 this.$selectedComponent = null;
 var $shaders = $('.live-edit-shader');

 $shaders.removeClass('live-edit-animatable');

 $shaders.hide();
 };


 proto.handleWindowResize = function (event) {
 if (this.$selectedComponent) {
 this.show(event, this.$selectedComponent)
 }
 };

 }($liveedit));
 */