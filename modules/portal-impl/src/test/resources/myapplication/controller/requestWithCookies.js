exports.get = function (req) {
    return {
        status: 200,
        body: req,
        cookies: {
            "plain1": "value1",
            "plain2": "value2",
            "complex1": {
                value: "value1",
                path: "/valid/path",
                domain: "enonic.com",
                comment: "Some cookie comments",
                maxAge: 2000,
                secure: false,
                httpOnly: false
            },
            "complex2": {
                value: "value2",
                path: "/valid/path",
                domain: "enonic.com",
                comment: "Some cookie comments",
                maxAge: 1000,
                secure: true,
                httpOnly: true
            },
            "empty": null
        }
    }
};
