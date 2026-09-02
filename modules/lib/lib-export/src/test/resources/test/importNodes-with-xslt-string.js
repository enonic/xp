var exportLib = require('/lib/xp/export');

// xslt as a plain file name is not supported; only application resource keys are
exportLib.importNodes({
    source: 'my-export',
    targetNodePath: '/content',
    xslt: 'transform.xslt'
});
