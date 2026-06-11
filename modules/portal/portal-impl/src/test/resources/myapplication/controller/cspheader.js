exports.get = function () {
    return {
        body: 'Hello',
        headers: {
            'Content-Security-Policy': "default-src 'none'; script-src 'self'",
            'content-security-policy-report-only': "script-src 'none'",
            'X-Custom': 'kept'
        }
    };
};
