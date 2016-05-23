function handleAuthenticateResponse(loginResult) {
    if (loginResult.authenticated) {
        location.reload();
    } else {
        $("#message-container").html("Login failed!");
        $("#password-input").focus();
        $("#username-input, #password-input, #login-button").addClass("invalid");
    }
}

function loginButtonClick() {
    if (checkFieldsEmpty()) {
        return;
    }

    $("#username-input, #password-input, #login-button").removeClass("invalid");

    var data = {
        user: $("#username-input").val(),
        password: $("#password-input").val()
    }
    $.ajax({
        url: CONFIG.appLoginServiceUrl,
        type: 'post',
        dataType: 'json',
        contentType: 'application/json',
        success: handleAuthenticateResponse,
        data: JSON.stringify(data)
    });
}

function checkFieldsEmpty() {
    return $("#username-input").val() === "" || $("#password-input").val() === "";
}

function onInputTyped(event) {
    $("#username-input, #password-input, #login-button").removeClass("invalid");

    var fieldsEmpty = checkFieldsEmpty();
    if (fieldsEmpty) {
        $("#login-button").hide();
        $("#message-container").html("");
    } else {
        $("#login-button").show();
        if (event.which !== 13) {
            $("#message-container").html("");
        }
    }
}


$("#login-button").click(function () {
    loginButtonClick();
    return false;
});

$("#username-input, #password-input").keyup(function (event) {
    onInputTyped(event);
});

$("#username-input").click();// for mobile devices
$("#username-input").focus();
checkLoginButtonInterval = setInterval(function () { //workaround to show login button when browser autofills inputs
    var fieldsEmpty = checkFieldsEmpty();
    if (!fieldsEmpty) {
        $("#login-button").show();
        clearInterval(checkLoginButtonInterval);
    }
}, 100);
