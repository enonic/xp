exports.filter = function (req, next) {
    let resp = next(req);
    resp.headers = {
        pleaseDontFail: undefined
    }
    return resp;
};
