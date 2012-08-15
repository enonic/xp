(function () {
    // Class definition (constructor function)
    var cursor = AdminLiveEdit.ui.Cursor = function () {
        this.registerSubscribers();
    };

    // Shorthand ref to the prototype
    var p = cursor.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.registerSubscribers = function () {
        var self = this;

        $liveedit.subscribe('/ui/highlighter/on-highlight', function ($component) {
            self.updateCursor.call(self, $component);
        });

        $liveedit.subscribe('/ui/componentselector/on-select', function ($component) {
            self.updateCursor.call(self, $component);
        });
    };


    p.updateCursor = function ($component) {
        var componentType = AdminLiveEdit.Util.getComponentType($component);
        var $body = $liveedit('body');
        var cursor = 'default';
        switch (componentType) {
        case 'region':
            cursor = 'pointer';
            break;
        case 'window':
            cursor = 'move';
            break;
        default:
            cursor = 'default';
        }
        $body.css('cursor', cursor);
    };

}());