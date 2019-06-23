$(function () {
    var authorizeId = $('#authorize_id').val();

    if (cookieEnabled()) {
        var username = $('#username');
        var password = $('#password');
        var captcha = $('#captcha');

        var userName = getCookie('userName');
        if (userName) {
            username.val(userName);
        }

        $('#captchaImg').click(function () {
            this.src = this.src.split('?')[0] + '?rnd=' + Math.random();
        });

        $('#loginSubmit').click(function () {
            if (!$.trim(username.val())) {
                $('#errorMsg').html('请输入账号');
                return;
            }
            if (!$.trim(password.val())) {
                $('#errorMsg').html('请输入密码');
                return;
            }

            if (captcha.length > 0 && !$.trim(captcha.val())) {
                $('#errorMsg').html('请输入验证码');
                return;
            }

            rememberMe();

            $('#loginForm').submit();
        });

        $('#mobileLoginSubmit').click(function () {
            var mobile = $('#mobile').val();
            var mobileCaptcha = $('#mobileCaptcha').val();
            if (!mobile) {
                $("#errorMsg").html("请输入手机号码");
                return;
            }
            if (!sentCaptcha) {
                $("#errorMsg").html("请先发送验证码");
                return;
            }
            if (!mobileCaptcha) {
                $("#errorMsg").html("请输入验证码");
                return;
            }

            var url = $(this).attr("href") + mobileCaptcha;

            $.ajax({
                type: "get",
                url: url,
                dataType: "text"
            }).done(function () {
                $('#mobileLoginForm').submit();
            }).fail(function () {
                $("#errorMsg").html("验证码错误");
            });
        });

        var sentCaptcha = false;
        $('#sendCaptcha').click(function () {
            var $this = $(this);
            var url = $this.attr("href");

            var mobile = $("#mobile").val();
            if (!mobile || !/^1[\d]{10}$/.test(mobile)) {
                $("#errorMsg").html("请输入手机号码");
                return;
            }

            $this.attr("disabled", true);
            $.ajax({
                type: "post",
                url: url,
                data: {authorize_id: authorizeId, mobile: mobile},
                dataType: "text"
            }).done(function () {
                sentCaptcha = true;
                $("#errorMsg").html("验证码发送成功");

                var seconds = 60;
                var timer = setInterval(function () {
                    --seconds;
                    $this.html(seconds + 'S');
                    if (seconds < 1) {
                        $this.attr("disabled", false);
                        $this.html('发送验证码');
                        clearInterval(timer);
                        timer = null;
                    }
                }, 1000);
            }).fail(function (res) {
                sentCaptcha = false;
                $this.attr("disabled", false);
                $("#errorMsg").html(res.responseText);
            });
        });

        $('#loginModeSwitch').click(function () {
            $('#loginForm').toggle();
            $('#mobileLoginForm').toggle();
        });
    } else {
        $('#errorMsg').html('您的浏览器禁用了cookie，您无法正常登录！');
    }
});

function rememberMe() {
    if ($('#rememberMe').prop('checked')) {
        var userName = $('#username').val();
        cookie('userName', userName, 7);
    } else {
        removeCookie('userName');
    }
}