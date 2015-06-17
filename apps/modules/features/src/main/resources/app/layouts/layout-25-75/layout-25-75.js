var thymeleaf = require('/lib/view/thymeleaf');

function handleGet(req) {
    var editMode = req.mode == 'edit';

    var content = execute('portal.getContent');
    var component = execute('portal.getComponent');

    var view = resolve('layout-25-75.html');
    var body = thymeleaf.render(view, {
        title: content.displayName,
        path: content.path,
        name: content.name,
        editable: editMode,
        resourcesPath: execute('portal.assetUrl', {}),
        component: component,
        leftRegion: component.regions["left"],
        rightRegion: component.regions["right"]
    });

    return {
        body: body,
        contentType: 'text/html'
    };
}

exports.get = handleGet;
