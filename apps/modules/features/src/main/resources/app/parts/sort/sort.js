var thymeleaf = require('/lib/view/thymeleaf');
var view = resolve('sort-test.html');
var service = require('service.js').service;

function handleGet(req) {

    var content = execute('portal.getContent');

    var currentPage = execute('portal.pageUrl', {
        path: content._path
    });

    var byDefault = execute('content.getChildren', {
        key: "/features/sorting/getchildren-test",
        start: 0,
        count: 1000
    });

    var byCreatedTime = execute('content.getChildren', {
        key: "/features/sorting/getchildren-test",
        start: 0,
        count: 1000,
        sort: 'createdTime DESC'
    });

    var byUpdateTime = execute('content.getChildren', {
        key: "/features/sorting/getchildren-test",
        start: 0,
        count: 1000,
        sort: 'modifiedTime DESC'
    });


    var params = {
        currentPage: currentPage,
        byCreatedTime: byCreatedTime,
        byUpdateTime: byUpdateTime,
        byDefault: byDefault
    };

    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;