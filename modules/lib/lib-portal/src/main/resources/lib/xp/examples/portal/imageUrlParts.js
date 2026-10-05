var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// Parts of the URL of a scaled image
var parts = portalLib.imageUrlParts({
    id: '11cc4e09-0d9d-4a4d-9a4b-1a3a8c2b6f3e',
    scale: 'block(1024,768)',
    quality: 85,
    project: 'myproject',
    branch: 'master'
});

// The root of the media APIs as the frontend serves them
var url = 'https://cdn.example.com/api' + parts.path + parts.queryString;
// END

t.assertEquals('/media:image/myproject/11cc4e09-0d9d-4a4d-9a4b-1a3a8c2b6f3e:hash/block-1024-768/photo.jpg', parts.path);
t.assertEquals('?quality=85', parts.queryString);
t.assertEquals('myproject', parts.context);
t.assertEquals('11cc4e09-0d9d-4a4d-9a4b-1a3a8c2b6f3e', parts.id);
t.assertEquals('hash', parts.fingerprint);
t.assertEquals('block-1024-768', parts.scale);
t.assertEquals('photo.jpg', parts.name);
t.assertEquals('https://cdn.example.com/api/media:image/myproject/11cc4e09-0d9d-4a4d-9a4b-1a3a8c2b6f3e:hash/block-1024-768/photo.jpg?quality=85', url);
