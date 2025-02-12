exports.responseProcessor = function (req, res) {
    var trackingScript = '<script src="http://some.cdn/js/tracker.js"></script>';

    // Check if contribution field exists, if not create it
    var bodyEnd = res.pageContributions.bodyEnd;
    if (!bodyEnd) {
        res.pageContributions.bodyEnd = [];
    }

    // Add contribution
    res.pageContributions.bodyEnd.push(trackingScript);

    return res;
};
