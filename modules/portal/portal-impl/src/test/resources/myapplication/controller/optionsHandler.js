exports.OPTIONS = () => ({
    status: 204,
    headers: {
        'Access-Control-Allow-Origin': 'https://example.com'
    }
});

exports.all = () => ({
    status: 200,
    body: 'all handler'
});
