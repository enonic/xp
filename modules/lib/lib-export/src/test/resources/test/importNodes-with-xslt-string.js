var exportLib = require('/lib/xp/export');

// xslt as a plain file name is deprecated and ignored; only application resource keys are supported
exportLib.importNodes({
    source: 'my-export',
    targetNodePath: '/content',
    xslt: 'transform.xslt'
});
