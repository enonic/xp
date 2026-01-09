exports.OPTIONS = function (req) {
    return {
        status: 200,
        headers: {
            'Allow': 'GET, POST, PUT, DELETE, OPTIONS'
        },
        body: 'OPTIONS handler'
    };
};

exports.PUT = function (req) {
    return {
        status: 200,
        body: 'PUT handler'
    };
};

exports.TRACE = function (req) {
    return {
        status: 200,
        body: 'TRACE handler'
    };
};
