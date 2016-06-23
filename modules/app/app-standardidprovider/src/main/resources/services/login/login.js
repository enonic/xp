var authLib = require('/lib/xp/auth');

function handlePost(req) {
    var body = JSON.parse(req.body);
    var loginResult = authLib.login({
        user: body.user,
        password: body.password,
        userStore: body.userStore
    });
    return {
        body: loginResult,
        contentType: 'application/json'
    };
}
exports.post = handlePost;
