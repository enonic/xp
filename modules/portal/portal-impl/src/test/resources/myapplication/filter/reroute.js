exports.filter = function (req, next) {
    req.contentPath = '/mysite/municipalities/oslo';
    return next(req);
};
