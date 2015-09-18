exports.get = function (req) {

    return {
        body: '<html>\n<head></head>\n<body>\n<h1>Title</h1>\n\n<p>Some text</p></body>\n</html>',
        contentType: 'text/html',
        filters: ['filter1', 'filter2', 'filter3']
    };

};
