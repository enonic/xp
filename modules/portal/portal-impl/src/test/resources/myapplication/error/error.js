exports.handle404 = function (err) {
    return {
        body: 'Something was not found',
        contentType: 'text/plain'
    }
};

exports.handleError = function (err) {
    return {
        body: 'Generic error...',
        contentType: 'text/plain'
    }
};