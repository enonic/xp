(function () {
    // Namespace
    AdminLiveEdit.ui = {};


    // Class definition (constructor function)
    AdminLiveEdit.ui.Base = function () {
        this.$element = $liveedit([]);
    };


    // Methods
    AdminLiveEdit.ui.Base.prototype = {
        counter: 0,
        blankImage: 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',

        createElement: function (htmlString) {
            var id = AdminLiveEdit.ui.Base.prototype.counter++;
            var $element = $liveedit(htmlString);
            $element.attr('id', 'live-edit-cmp-' + id);
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
        }
    };

}());