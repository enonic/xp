var thymeleaf = require('/lib/view/thymeleaf');

exports.get = function (req) {
    var editMode = req.mode == 'edit';

    var content = execute('portal.getContent');
    var component = execute('portal.getComponent');

    var view = resolve('layout-3-col.html');
    var body = thymeleaf.render(view, {
        title: content.displayName,
        path: content.path,
        name: content.name,
        editable: editMode,
        resourcesPath: execute('portal.assetUrl', {}),
        component: component,
        leftRegion: component.regions["left"],
        centerRegion: component.regions["center"],
        rightRegion: component.regions["right"]
    });

    return {
        body: body,
        contentType: 'text/html'
    };
};
