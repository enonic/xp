var thymeleaf = require('view/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'content.page.html');
var stk = require('stk/stk');

function handleGet(req) {
    var site = execute('portal.getSite');
    var content = execute('portal.getContent');
    var postUrl = stk.serviceUrl("content", {});

    if (req.params && req.params.contentId) {
        stk.log("Loading content with Id " + req.parameters.contentId);

        var result = execute('content.get', {
            key: req.params.contentId
        });

        stk.log(result);
    }

    var params = {
        post: content.data,
        pageTemplate: content.type == 'portal:page-template' ? true : false,
        site: site,
        content: content,
        postUrl: postUrl
    };

    return stk.view.render(view, params);
}

exports.get = handleGet;
