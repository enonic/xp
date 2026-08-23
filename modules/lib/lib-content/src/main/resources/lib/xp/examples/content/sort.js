var contentLib = require('/lib/xp/content');
var assert = require('/lib/xp/testing');

// The reorder anchors are the _orderKey values read off the sibling contents
// surrounding the drop point.
var above = {_orderKey: '3ala4x8dnbc.above-sibling-id'};
var below = {_orderKey: '3ala4xa1kfj.below-sibling-id'};

// BEGIN
// Order children alphabetically by display name.
var byName = contentLib.sort({
    key: '/my-site/my-list',
    childOrder: 'displayName ASC'
});
// END

// BEGIN
// Switch children to manual ordering. The order they are in stays as it is.
var manual = contentLib.sort({
    key: '/my-site/my-list',
    childOrder: 'manual'
});
// END

// BEGIN
// Drop a child between two of its siblings: 'afterOrderKey' is the '_orderKey' of
// the sibling directly above the drop point, 'beforeOrderKey' of the one below it.
var moved = contentLib.sort({
    key: '/my-site/my-list',
    reorder: [{
        contentId: 'b7fd8fa8-5bda-4933-a04c-a3b46bccc4fa',
        afterOrderKey: above._orderKey,
        beforeOrderKey: below._orderKey
    }]
});

log.info('Repositioned ' + moved.movedChildren.length + ' children');
// END

assert.assertEquals('/a/b/mycontent', byName.content._path);
assert.assertEquals('/a/b/mycontent', manual.content._path);
assert.assertEquals(1, moved.movedChildren.length);
assert.assertEquals('b7fd8fa8-5bda-4933-a04c-a3b46bccc4fa', moved.movedChildren[0]);
