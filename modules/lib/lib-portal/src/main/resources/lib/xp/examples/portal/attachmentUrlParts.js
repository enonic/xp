var portalLib = require('/lib/xp/portal');
var t = require('/lib/xp/testing');

// BEGIN
// Parts of the URL of an attachment, to be downloaded
var parts = portalLib.attachmentUrlParts({
    path: '/my-site/documents/report',
    download: true,
    project: 'myproject',
    branch: 'draft'
});

// The root of the media APIs as the frontend serves them
var url = 'https://cdn.example.com/api' + parts.path + parts.queryString;
// END

t.assertEquals('/media:attachment/myproject:draft/reportid:hash/report.pdf', parts.path);
t.assertEquals('?download', parts.queryString);
t.assertEquals('myproject:draft', parts.context);
t.assertEquals('reportid', parts.id);
t.assertEquals('hash', parts.fingerprint);
t.assertEquals('report.pdf', parts.name);
t.assertEquals('https://cdn.example.com/api/media:attachment/myproject:draft/reportid:hash/report.pdf?download', url);
