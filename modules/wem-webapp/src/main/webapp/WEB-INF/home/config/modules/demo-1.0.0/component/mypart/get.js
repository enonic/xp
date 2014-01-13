var component = portal.component;

portal.response.body = 'Part component: ' + component.name;
portal.response.contentType = 'text/html';
portal.response.status = 200;
