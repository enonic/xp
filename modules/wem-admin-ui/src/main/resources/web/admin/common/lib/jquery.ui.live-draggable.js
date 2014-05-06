(function ($) {
    $.fn.liveDraggable = function (opts) {
        var el = this;
        $(document).on("mouseover", el, function (e) {
            if (!$(el.selector).data("init")) {
                $(el.selector).data("init", true).draggable(opts);
            }
        });
        return this;
    };
}(jQuery));
