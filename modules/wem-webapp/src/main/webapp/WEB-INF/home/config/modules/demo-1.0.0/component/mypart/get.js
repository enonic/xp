var content = portal.content;

portal.response.body = 'Part component: ' + content.path;
portal.response.contentType = 'text/html';
portal.response.status = 200;
