var statusUrl = "/admin/rest/status";
var adminToolUrl = "/admin/tool"
var connectionLostMessageId;

function doPoll() {

    var request = createGetStatusRequest();

    request.onreadystatechange = function () {
        if (request.readyState == 4) {
            if (request.status >= 200 && request.status < 300) {
                checkAuthenticated(request.response);
            }
            else {
                alertConnectionLost();
            }
        }
    }

    request.send();
}

function createGetStatusRequest() {
    var xhr = new XMLHttpRequest();
    xhr.timeout = 10000;
    xhr.open('GET', statusUrl, true);

    return xhr;
}

function checkAuthenticated(response) {
    var json = JSON.parse(response);
    var authenticated = json && json.context && json.context.authenticated;

    if (!authenticated) {
        logout();
    }
}

function logout() {
    window.location.href = adminToolUrl;
}

function alertConnectionLost() {
    if (!connectionLostMessageId) {
        connectionLostMessageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    }

}

exports.startPolling = function () {
    setInterval(doPoll, 15000);
};

