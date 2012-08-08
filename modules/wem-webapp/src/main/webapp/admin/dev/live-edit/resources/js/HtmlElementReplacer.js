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
        var $elements = $liveedit('iframe, object');

        $elements.each(function (i) {
            self.replace($liveedit(this));
        });
    };


    p.replace = function ($element) {
        this.hideElement($element);
        this.createPlaceholder($element).insertAfter($element);
    };


    p.createPlaceholder = function ($element) {
        var dummyText = '(' + $element[0].tagName.toLowerCase() + ' replaced with div)';
        var $placeholder = $liveedit('<div>' + dummyText + '</div>');
        $placeholder.addClass('live-edit-html-element-placeholder');
        $placeholder.width(this.getWidth($element));
        $placeholder.height(this.getHeight($element));
        return $placeholder;
    };


    p.hideElement = function ($element) {
        $element.hide();
    };


    p.getWidth = function ($element) {
        var attrWidth = $element.attr('width');
        if (!attrWidth) {
            // Return computed style width (int/pixels);
            return $element.width();
        }
        return attrWidth;
    };


    p.getHeight = function ($element) {
        var attrHeight = $element.attr('height');
        if (!attrHeight) {
            // Return computed style height (int/pixels);
            return $element.height();
        }
        return attrHeight;
    };

}());