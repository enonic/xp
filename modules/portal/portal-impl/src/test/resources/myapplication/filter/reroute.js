exports.filter = function (req, next) {
    req.rawPath = '/site/myproject/draft/mysite/municipalities/oslo';
    return next(req);
};
