(function ($) {
    console.log('Init Live Draggable. Using jQuery ' + $().jquery);

    $.fn.liveDraggable = function (opts) {
        this.live("mouseover", function () {
            if (!$(this).data("init")) {
                $(this).data("init", true).draggable(opts);
            }
        });
        return this;
    };
}(jQuery));
