var exportLib = require('/lib/xp/export');

exportLib.importNodes({
    source: 'my-export',
    targetNodePath: '/content',
    xslt: 'transform.xslt'
});
