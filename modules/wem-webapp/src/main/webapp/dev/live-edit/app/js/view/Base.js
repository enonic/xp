(function ($) {
    'use strict';

    // Namespace
    AdminLiveEdit.view = {};


    // Class definition (constructor function)
    AdminLiveEdit.view.Base = function () {
        this.$element = $([]);
    };


    // Methods
    AdminLiveEdit.view.Base.prototype = {
        counter: 0,
        blankImage: 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',

        createElement: function (htmlString) {
            var id = AdminLiveEdit.view.Base.prototype.counter++;
            var $element = $(htmlString);
            $element.attr('id', 'live-edit-ui-cmp-' + id);
            this.$element = $element;

            return this.$element;
        },


        appendTo: function ($parent) {
            if ($parent.length > 0 && this.$element.length > 0) {
                $parent.append(this.$element);
            }
        },


        getEl: function () {
            return this.$element;
        },

        // TODO: This method does not belong here. Move to Util
        setCssPosition: function ($component) {
            var componentInfo = AdminLiveEdit.Util.getComponentInfo($component);
            if (componentInfo.tagName === 'body' && componentInfo.type === 'page') {
                this.getEl().css('position', 'fixed');
            } else {
                this.getEl().css('position', 'absolute');
            }
        }
    };

}($liveedit));