(function ($) {
    'use strict';

    // Class definition (constructor function)
    var mutationObserver = AdminLiveEdit.MutationObserver = function () {
        this.mutationObserver = null;
        this.$observedComponent = null;
        this.bindGlobalEvents();
    };

    // Fix constructor
    mutationObserver.constructor = mutationObserver;

    // Shorthand ref to the prototype
    var proto = mutationObserver.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.bindGlobalEvents = function () {
        $(window).on('component:select', $.proxy(this.observe, this));
        $(window).on('component:deselect', $.proxy(this.disconnect, this));
    };


    proto.observe = function (event, $selectedComponent) {
        var me = this;

        var isAlreadyObserved = me.$observedComponent && me.$observedComponent[0] === $selectedComponent[0];
        if (isAlreadyObserved) {
            return;
        }
        me.$observedComponent = $selectedComponent;

        me.mutationObserver = new AdminLiveEdit.MutationSummary({
            callback: function (summaries) {
                if (summaries && summaries[0]) {
                    $(window).trigger('component:select', [$(summaries[0].target)]);
                }
            },
            rootNode: $selectedComponent[0],
            queries: [{ all: true}]
        });
    };


    proto.disconnect = function () {
        console.info('disconnect mutation observer');
        this.$observedComponent = null;
        if (this.mutationObserver) {
            this.mutationObserver.disconnect();
        }
    };


}($liveedit));