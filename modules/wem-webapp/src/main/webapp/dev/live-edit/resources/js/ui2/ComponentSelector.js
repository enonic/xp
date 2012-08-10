(function () {
    // Class definition (constructor function)
    var componentSelector = AdminLiveEdit.ui2.ComponentSelector = function () {
        this.$selectedComponent = $liveedit([]); // Empty jQuery object
        this.create();
        this.registerSubscribers();
    };

    // Inherits ui.Base
    componentSelector.prototype = new AdminLiveEdit.ui2.Base();

    // Fix constructor as it now is Base
    componentSelector.constructor = componentSelector;

    // Shorthand ref to the prototype
    var p = componentSelector.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/ui/componentselector/on-select', function ($component) {
            self.select.call(self, $component);
        });

        $liveedit.subscribe('/ui/componentselector/on-select-parent', function () {
            self.selectParent.call(self);
        });

        $liveedit.subscribe('/ui/componentselector/on-deselect', function () {
            self.deselect.call(self);
        });

        $liveedit.subscribe('/ui/dragdrop/on-sortstop', function (uiEvent, ui) {
            $liveedit.publish('/ui/componentselector/on-select', [ui.item]);
        });
    };


    p.create = function () {
        var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-selected-border">' +
                   '    <rect width="150" height="150"/>' +
                   '</svg>';

        this.createElement(html);
        this.appendTo($liveedit('body'));
    };


    p.getSelected = function () {
        return this.$selectedComponent;
    };


    p.setSelected = function ($component) {
        this.$selectedComponent = $component;
    };


    p.hide = function () {
        var $el = this.getEl();
        $liveedit('body').append(this.getEl());

        $el.css({
            top: '-5000px',
            left: '-5000px'
        });
    };


    p.scrollComponentIntoView = function ($component) {
        var componentTopPosition = util.getPageComponentPagePosition($component).top;
        if (componentTopPosition <= window.pageYOffset) {
            $liveedit('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    };


    p.select = function ($component) {
        var $el = this.getEl();

        // Add position relative to the page component in order have absolute positioned elements inside.
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');

        this.setSelected($component);
        this.scrollComponentIntoView($component);
    };


    p.selectParent = function () {
        var $parent = this.getSelected().parents('[data-live-edit-type]');
        if ($parent && $parent.length > 0) {
            $liveedit.publish('/ui/componentselector/on-select', [$liveedit($parent[0])]);
        }
    };


    p.deselect = function () {
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        this.hide();
        this.setSelected($liveedit([]));
    };

}());