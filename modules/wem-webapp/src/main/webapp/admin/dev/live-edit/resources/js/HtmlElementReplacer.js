(function () {
    // Class definition (constructor function)
    var htmlElementReplacer = AdminLiveEdit.HtmlElementReplacer = function () {
        this.replaceElementsWithPlaceholders();
    };

    // Fix constructor
    htmlElementReplacer.constructor = htmlElementReplacer;

    // Shorthand ref to the prototype
    var p = htmlElementReplacer.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.registerSubscribers = function () {
    };


    p.replaceElementsWithPlaceholders = function () {
        var self = this;
        var $elements = $liveedit('[data-live-edit-type=window] > iframe, object');
        $elements.each(function (i) {
            self.replace($liveedit(this));
        });
    };


    p.replace = function ($element) {
        this.hideElement($element);
        this.createPlaceholder($element).insertAfter($element);
    };


    p.createPlaceholder = function ($element) {
        var $placeholder = $liveedit('<div></div>');
        $placeholder.addClass('live-edit-html-element-placeholder');
        $placeholder.width(this.getWidth($element));
        $placeholder.height(this.getHeight($element));

        var $icon = $liveedit('<div/>');
        $icon.addClass(this.resolveIconCssClass($element));
        $icon.append('<div>' + $element[0].tagName.toLowerCase() + '</div>');
        $placeholder.append($icon);

        return $placeholder;
    };


    p.getWidth = function ($element) {
        var attrWidth = $element.attr('width');
        if (!attrWidth) {
            // Return computed style width (int/pixels);
            // -2 for placeholder border
            return $element.width() - 2;
        }
        return attrWidth;
    };


    p.getHeight = function ($element) {
        var attrHeight = $element.attr('height');
        if (!attrHeight) {
            // Return computed style height (int/pixels);
            // -2 for placeholder border
            return $element.height() - 2;
        }
        return attrHeight;
    };


    p.hideElement = function ($element) {
        $element.hide();
    };


    p.resolveIconCssClass = function ($element) {
        var tagName = $element[0].tagName.toLowerCase();
        var clsName = '';
        if (tagName === 'iframe') {
            clsName = 'live-edit-iframe';
        } else {
            clsName = 'live-edit-object';
        }
        return clsName;
    };

}());