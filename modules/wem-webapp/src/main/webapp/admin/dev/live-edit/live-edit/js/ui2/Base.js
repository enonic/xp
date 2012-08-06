// Namespace
AdminLiveEdit.ui2 = {};

// Class
AdminLiveEdit.ui2.Base = function () {
    this.$element = $liveedit([]);
};


// Methods
AdminLiveEdit.ui2.Base.prototype = {
    createElement: function (htmlString) {
        var $element = $liveedit(htmlString);
        $element.attr('id', 'live-edit-cmp-' + AdminLiveEdit.Util.createGUID());
        this.$element = $element;
        console.log(AdminLiveEdit.ui2.Base.prototype.counter);
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