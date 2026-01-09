// Example demonstrating new uppercase HTTP method names (recommended)
// This avoids conflicts with the 'delete' reserved word and imported functions like 'get'

exports.GET = function (req) {
    return {
        status: 200,
        body: 'Using uppercase GET'
    }
};

exports.POST = function (req) {
    return {
        status: 201,
        body: 'Using uppercase POST'
    }
};

exports.DELETE = function (req) {
    // No conflict with 'delete' reserved word
    return {
        status: 200,
        body: 'Using uppercase DELETE'
    }
};

exports.PUT = function (req) {
    return {
        status: 200,
        body: 'Using uppercase PUT'
    }
};

exports.PATCH = function (req) {
    return {
        status: 200,
        body: 'Using uppercase PATCH'
    }
};

// Fallback for all other HTTP methods
exports.ALL = function (req) {
    return {
        status: 200,
        body: 'Fallback ALL handler'
    }
};
