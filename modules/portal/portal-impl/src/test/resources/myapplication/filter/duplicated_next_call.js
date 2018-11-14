exports.filter = function (req, next) {
    var resp = next(req);
    return next(req);
};
