function ResponsiveRange(minRange, maxRange) {
    this.minRange = minRange;
    this.maxRange = maxRange;
    this.cls = '_' + this.minRange + '-' + this.maxRange;

    this.isFit = function (width) {
        return (this.minRange <= width) && (width <= this.maxRange);
    }
}

var responsiveRanges = [
    new ResponsiveRange(0, 240),
    new ResponsiveRange(240, 360),
    new ResponsiveRange(360, 540),
    new ResponsiveRange(540, 720),
    new ResponsiveRange(720, 960),
    new ResponsiveRange(960, 1200),
    new ResponsiveRange(1200, 1380),
    new ResponsiveRange(1380, 1620),
    new ResponsiveRange(1620, 1920),
    new ResponsiveRange(1920, 10000)
];

exports.applyResponsiveCls = function () {
    responsiveRanges.forEach(function (responsiveRange) {
        if (responsiveRange.isFit(document.body.clientWidth)) {
            document.body.classList.add(responsiveRange.cls);
        }
        else if (document.body.classList.contains(responsiveRange.cls)) {
            document.body.classList.remove(responsiveRange.cls)
        }
    });
};
