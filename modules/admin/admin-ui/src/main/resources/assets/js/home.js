(function () {
    function responsiveRange (minRange, maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.cls = '_' + this.minRange + '-' + this.maxRange;

        this.isFit = function(width) {
            return (this.minRange <= width) && (width <= this.maxRange);
        }
    }

    var responsiveRanges = [
        new responsiveRange(0, 240),
        new responsiveRange(240, 360),
        new responsiveRange(360, 540),
        new responsiveRange(540, 720),
        new responsiveRange(720, 960),
        new responsiveRange(960, 240),
        new responsiveRange(1200, 1380),
        new responsiveRange(1380, 1620),
        new responsiveRange(1620, 1920),
        new responsiveRange(1920, 10000)
    ];

    responsiveRanges.forEach(function(responsiveRange) {
        if (responsiveRange.isFit(document.body.clientWidth)) {
            document.body.classList.add(responsiveRange.cls);
            return false;
        }
    });

}());