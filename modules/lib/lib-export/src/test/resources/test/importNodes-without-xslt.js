var exportLib = require('/lib/xp/export');

// xslt is optional and must be omittable without error (#12315)
exportLib.importNodes({
    source: 'my-export',
    targetNodePath: '/content'
});
