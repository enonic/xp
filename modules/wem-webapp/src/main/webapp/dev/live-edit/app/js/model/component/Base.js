AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.model.component');

(function ($) {
    'use strict';

    AdminLiveEdit.model.component.Base = function () {
        this.cssSelector = '';
    };


    AdminLiveEdit.model.component.Base.prototype = {

        attachMouseOverEvent: function () {
            var me = this;

            $(document).on('mouseover', me.cssSelector, function (event) {

                var $component = $(this);

                var targetIsUiComponent = me.isLiveEditUiComponent($(event.target));
                var cancelEvents = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDropSort.isDragging();
                if (cancelEvents) {
                    return;
                }
                event.stopPropagation();

                $(window).trigger('component.mouseOver', [$component]);
            });
        },


        attachMouseOutEvent: function () {
            var me = this;

            $(document).on('mouseout', function () {
                if (me.hasComponentSelected()) {
                    return;
                }
                $(window).trigger('component.mouseOut');
            });
        },


        attachClickEvent: function () {
            var me = this;

            $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
                if (me.isLiveEditUiComponent($(event.target))) {
                    return;
                }

                event.stopPropagation();
                event.preventDefault();

                var $component = $(event.currentTarget),
                    componentIsSelected = $component.hasClass('live-edit-selected-component'),
                    pageHasComponentSelected = $('.live-edit-selected-component').length > 0;

                if (componentIsSelected || pageHasComponentSelected) {
                    $(window).trigger('component.onDeSelect');
                } else {

                    // Used by eg. Menu
                    var pagePosition = {
                        x: event.pageX,
                        y: event.pageY
                    };

                    $(window).trigger('component.onSelect', [$component, pagePosition]);
                }
            });
        },


        hasComponentSelected: function () {
            return $('.live-edit-selected-component').length > 0;
        },


        isLiveEditUiComponent: function ($target) {
            return $target.is('[id*=live-edit-ui-cmp]') || $target.parents('[id*=live-edit-ui-cmp]').length > 0;
        },


        getAll: function () {
            return $(this.cssSelector);
        }

    };
}($liveedit));
