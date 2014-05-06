(function ($) {
    $.fn.liveDraggable = function (opts) {
        this.on("mouseover", function () {
            if (!$(this).data("init")) {
                $(this).data("init", true).draggable(opts);
            }
        });
        return this;
    };
}(jQuery));
