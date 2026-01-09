exports.GET = function (req) {
    return {
        status: 200,
        body: 'GET handler'
    }
};

exports.POST = function (req) {
    return {
        status: 201,
        body: 'POST handler'
    }
};

exports.DELETE = function (req) {
    return {
        status: 200,
        body: 'DELETE handler'
    }
};

exports.PATCH = function (req) {
    return {
        status: 200,
        body: 'PATCH handler'
    }
};
