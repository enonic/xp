exports.get = function (req) {

    return {
        body: '<body/>',
        contentType: 'text/html',
        contribute: {
            head: '<script src="test.js" />',
            foot: '<link href="styles.css" rel="stylesheet" />'
        }
    };
};
