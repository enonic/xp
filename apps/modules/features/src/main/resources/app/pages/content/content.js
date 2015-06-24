var portal = require('/lib/xp/portal');
var thymeleaf = require('/lib/xp/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'content.page.html');
var stk = require('stk/stk');

function handleGet(req) {
    var site = portal.getSite();
    var content = portal.getContent();
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
        pageTemplate: content.type === 'portal:page-template',
        site: site,
        content: content,
        postUrl: postUrl
    };

    return thymeleaf.render(view, params);
}

exports.get = handleGet;
