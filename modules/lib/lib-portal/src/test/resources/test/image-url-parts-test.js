var portal = require('/lib/xp/portal.js');

exports.withoutScale = function () {
    portal.imageUrlParts({id: '123456', project: 'myproject', branch: 'master'});
};
