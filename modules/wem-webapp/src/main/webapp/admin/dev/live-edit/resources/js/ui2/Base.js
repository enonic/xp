// Namespace
AdminLiveEdit.ui2 = {};

// Class
AdminLiveEdit.ui2.Base = function () {
    this.$element = $liveedit([]);
};


// Methods
AdminLiveEdit.ui2.Base.prototype = {
    blankImage: 'data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==',

    createElement: function (htmlString) {
        var $element = $liveedit(htmlString);
        $element.attr('id', 'live-edit-cmp-' + AdminLiveEdit.Util.createGUID());
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