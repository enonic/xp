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
        var me = this;
        $(window).on('component:mouseover', $.proxy(me.observe, me));
        $(window).on('component:mouseout', $.proxy(me.disconnect, me));
        $(window).on('component:select', $.proxy(me.observe, me));
        $(window).on('component:deselect', $.proxy(me.disconnect, me));
    };


    proto.observe = function (event, $component) {
        var me = this;

        var isAlreadyObserved = me.$observedComponent && me.$observedComponent[0] === $component[0];
        if (isAlreadyObserved) {
            return;
        }
        me.$observedComponent = $component;

        me.mutationObserver = new AdminLiveEdit.MutationSummary({
            callback: function (summaries) {
                console.log('mutation!', summaries);
                me.onMutate(summaries, event);
            },
            rootNode: $component[0],
            queries: [{ all: true}]
        });
    };


    proto.onMutate = function (summaries, event) {
        if (summaries && summaries[0]) {
            var $targetComponent = $(summaries[0].target);
            var targetComponentIsSelected = $targetComponent.hasClass('live-edit-selected-component');
            var isMouseOverEventAndTargetComponentIsNotSelected = event.type === 'component:mouseover' && !targetComponentIsSelected;

            if (isMouseOverEventAndTargetComponentIsNotSelected) {
                $(window).trigger('component:mouseover', [$targetComponent]);
            } else {
                $(window).trigger('component:select', [$targetComponent]);
            }
        }
    };


    proto.disconnect = function (event) {
        var targetComponentIsSelected = (this.$observedComponent && this.$observedComponent.hasClass('live-edit-selected-component'));
        var isMouseOutEventAndComponentIsSelected = event.type === 'component:mouseout' && targetComponentIsSelected;
        if (isMouseOutEventAndComponentIsSelected) {
            return;
        }

        console.log('Mutation Observer disconnect');

        this.$observedComponent = null;
        if (this.mutationObserver) {
            this.mutationObserver.disconnect();
            this.mutationObserver = null;
        }
    };

}($liveedit));