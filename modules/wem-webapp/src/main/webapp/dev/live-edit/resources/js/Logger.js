(function () {
    // Class definition (constructor function)
    var logger = AdminLiveEdit.Logger = function () {
        this.$logger = null;
    };

    // Fix constructor
    logger.constructor = logger;

    // Shorthand ref to the prototype
    var p = logger.prototype;

    // Uses
    var util = AdminLiveEdit.Util;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.registerSubscribers = function () {
        var self = this;
        $liveedit.subscribe('/ui/componentselector/on-select', function ($component) {
            self.addMessage.call(self, 'on-select: ' + '(' + util.getComponentType($component) + ') ' + util.getComponentName($component));
        });

        $liveedit.subscribe('/ui/selectedcomponent/on-deselect', function () {
            self.addMessage.call(self, 'on-deselect');
        });

        $liveedit.subscribe('/ui/dragdrop/on-sortstart', function (event, ui) {
            self.addMessage.call(self, 'on-sortstart: ' + util.getComponentName(ui.item));
        });

        $liveedit.subscribe('/ui/dragdrop/on-sortstop', function (event, ui) {
            self.addMessage.call(self, 'on-sortstop: ' + util.getComponentName(ui.item));
        });
    };


    p.create = function () {
        this.registerSubscribers();
    };


    p.addMessage = function (message) {
        console.info(message);
    };

}());