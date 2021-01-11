exports.handleError = function (err) {
    return {
        status: 301,
        headers: {
            Location: '/another/page'
        }
    };
};
