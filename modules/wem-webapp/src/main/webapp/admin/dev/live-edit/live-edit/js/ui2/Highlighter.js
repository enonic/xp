(function () {
    // Class definition (constructor function)
    var highlighter = AdminLiveEdit.ui.Highlighter = function () {
        this.create();
    };

    // Inherits ui.Base
    highlighter.prototype = new AdminLiveEdit.ui.Base();

    // Fix constructor as it now is Base
    highlighter.constructor = highlighter;

    // Shorthand ref to the prototype
    var p = highlighter.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    var PAGE_BORDER_COLOR = '#141414';
    var REGION_BORDER_COLOR = '#141414';
    var WINDOW_BORDER_COLOR = '#141414';
    var CONTENT_BORDER_COLOR = '#141414';
    var PARAGRAPH_BORDER_COLOR = '#141414';


    p.registerSubscribers = function () {
        $liveedit.subscribe('/page/component/highlight', this.highlight);
        $liveedit.subscribe('/page/component/hide-highlighter', this.hide);
    };


    p.create = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" id="live-edit-highlighter" style="top:-5000px;left:-5000px">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';
        this.createElement(html);
        this.appendTo($liveedit('body'));
    };


    p.highlight = function ($component) {
        var componentType = util.getTypeFromComponent($component);
        var tagName = util.getTagNameForComponent($component);
        var componentBoxModel = util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        // We need to get the full height of the page/document.
        if (componentType === 'page' && tagName === 'body') {
            h = AdminLiveEdit.Util.getDocumentSize().height;
        }

        var $highlighter = $liveedit('#live-edit-highlighter');
        var $highlighterRect = $highlighter.find('rect');

        $highlighter.width(w);
        $highlighter.height(h);
        $highlighterRect[0].setAttribute('width', w);
        $highlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top : top,
            left: left
        });

        switch (componentType) {
        case 'region':
            $highlighter.css('stroke', REGION_BORDER_COLOR);
            break;
        case 'window':
            $highlighter.css('stroke', WINDOW_BORDER_COLOR);
            break;
        case 'content':
            $highlighter.css('stroke', CONTENT_BORDER_COLOR);
            break;
        case 'paragraph':
            $highlighter.css('stroke', PARAGRAPH_BORDER_COLOR);
            break;
        case 'page':
            $highlighter.css('stroke', PAGE_BORDER_COLOR);
            break;
        default:
            $highlighter.css('stroke', 'red');
        }
    };


    p.hide = function () {
        this.getEl().css({
            top: '-5000px',
            left: '-5000px'
        });
    };

}());