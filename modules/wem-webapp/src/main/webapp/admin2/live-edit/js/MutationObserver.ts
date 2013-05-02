AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.MutationObserver');

(function ($) {
    'use strict';

    // Class definition (constructor function)
    var mutationObserver = AdminLiveEdit.MutationObserver = function () {
        this.mutationObserver = null;
        this.$observedComponent = null;
        this.registerGlobalListeners();
    };

    // Fix constructor
    // mutationObserver.constructor = mutationObserver;

    // Shorthand ref to the prototype
    var proto = mutationObserver.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    proto.registerGlobalListeners = function () {
        var me = this;
        /*$(window).on('component.mouseOver', $.proxy(me.observe, me));
        $(window).on('component.mouseOut', $.proxy(me.disconnect, me));
        $(window).on('component.onSelect', $.proxy(me.observe, me));
        $(window).on('component.onDeselect', $.proxy(me.disconnect, me));*/

        $(window).on('component.onParagraphEdit', $.proxy(me.observe, me));
        $(window).on('shader.onClick', $.proxy(me.disconnect, me));

    };


    proto.observe = function (event, $component) {
        var me = this;

        var isAlreadyObserved = me.$observedComponent && me.$observedComponent[0] === $component[0];
        if (isAlreadyObserved) {
            return;
        }
        me.disconnect(event);

        me.$observedComponent = $component;

        me.mutationObserver = new LiveEditMutationSummary({
            callback: function (summaries) {
                me.onMutate(summaries, event);
            },
            rootNode: $component[0],
            queries: [{ all: true}]
        });
    };


    // Called when the html in the observed component mutates
    proto.onMutate = function (summaries, event) {
        if (summaries && summaries[0]) {
            var $targetComponent = $(summaries[0].target),
                targetComponentIsSelected = $targetComponent.hasClass('live-edit-selected-component'),
                componentIsNotSelectedAndMouseIsOver = !targetComponentIsSelected && event.type === 'component.mouseOver',
                componentIsParagraphAndBeingEdited = $targetComponent.attr('contenteditable');

            if (componentIsParagraphAndBeingEdited) {
                $(window).trigger('component.onParagraphEdit', [$targetComponent]);
            } else if (componentIsNotSelectedAndMouseIsOver) {
                $(window).trigger('component.mouseOver', [$targetComponent]);
            } else {
                $(window).trigger('component.onSelect', [$targetComponent]);
            }
        }
    };


    proto.disconnect = function (event) {
        var targetComponentIsSelected = (this.$observedComponent && this.$observedComponent.hasClass('live-edit-selected-component'));
        var componentIsSelectedAndUserMouseOut = event.type === 'component.mouseOut' && targetComponentIsSelected;
        if (componentIsSelectedAndUserMouseOut) {
            return;
        }

        this.$observedComponent = null;
        if (this.mutationObserver) {
            this.mutationObserver.disconnect();
            this.mutationObserver = null;
        }
    };

}($liveedit));