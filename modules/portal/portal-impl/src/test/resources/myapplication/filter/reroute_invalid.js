exports.filter = function (req, next) {
    req.rawPath = '/webapp/otherapp/path';
    return next(req);
};
