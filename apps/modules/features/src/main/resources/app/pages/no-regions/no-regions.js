var portal = require('/lib/xp/portal');
var menuLib = require('menu.js');
var thymeleaf = require('/lib/xp/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'no-region.page.html');

function handleGet(req) {

    var editMode = req.mode == 'edit';

    var site = portal.getSite();
    var reqContent = portal.getContent();
    var params = {
        context: req,
        site: site,
        reqContent: reqContent,
        editable: editMode,
        siteMenuItems: menuLib.getSiteMenu(10)
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
