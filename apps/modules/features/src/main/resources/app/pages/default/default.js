var menuLib = require('/lib/menu.js');
var thymeleaf = require('/lib/view/thymeleaf');
var parentPath = './';
var view = resolve(parentPath + 'default.page.html');

function handleGet(req) {

    var editMode = req.mode == 'edit';

    var site = execute('portal.getSite');
    var reqContent = execute('portal.getContent');
    var params = {
        context: req,
        site: site,
        reqContent: reqContent,
        mainRegion: reqContent.page.regions["main"],
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


