var portal = require('/lib/xp/portal');
var thymeleaf = require('view/thymeleaf');

exports.get = function(req) {

    var content = portal.getContent();
    var view = resolve('tinymce.html');

    log.info("content %s", JSON.stringify(content, null, 4));

    var params = {
        content: content,
        tinymcevalue: content.data.htmlarea_text
    };

    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };

};
