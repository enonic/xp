var editMode = portal.request.mode == 'edit';

var content = portal.content;
var body = system.mustache.render('views/frogger.html', {
    title: content.displayName,
    path: content.path,
    name: content.name,
    editable: editMode,
    resourcesPath: portal.url.createResourceUrl('')
});

portal.response.body = body;
portal.response.contentType = 'text/html';
portal.response.status = 200;
