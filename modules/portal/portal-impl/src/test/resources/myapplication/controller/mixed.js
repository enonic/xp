// Test backward compatibility - mixing uppercase and lowercase
exports.GET = function (req) {
    return {
        status: 200,
        body: 'GET uppercase'
    }
};

exports.post = function (req) {
    return {
        status: 201,
        body: 'post lowercase'
    }
};
