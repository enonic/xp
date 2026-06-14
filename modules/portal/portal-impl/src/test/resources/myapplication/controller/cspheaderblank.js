exports.get = function () {
    return {
        body: 'Hello',
        headers: {
            'Content-Security-Policy': '',
            'content-security-policy-report-only': null,
            'X-Custom': 'kept'
        }
    };
};
