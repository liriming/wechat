var proxyhost = "http://120.79.53.150:8080";
var startTaskTime = (new Date()).getTime();
var number = 0;

window.setInterval(function(){
    if(document.location.href.indexOf("fasttask/index") !== -1){

        if(number >= 6){
            var t = "/index.php/withdraw/index";
            mainView.router.reloadPage(t);
        }

        var t = "/index.php/fasttask/index?timestamp=" +new Date().getTime();
        mainView.router.reloadPage(t);
        sleep(1);
        var reservedul = $$(".type-reserved");
        var reservedli = reservedul[0].getElementsByTagName("li");
        var reservedA = reservedli[1].getElementsByTagName("a");
        if(reservedA.length !== 0){
            reservedli[1].getElementsByTagName("a")[0].click();
        }

        var ul = $$(".type-fast");
        var li = ul[0].getElementsByTagName("li");
        for(var i = 1; i < li.length; i++){
            if(parseFloat(li[i].getElementsByClassName("ff-ppnum")[0].innerHTML) < 10.00
                && parseInt(li[i].getElementsByClassName("item-title unit ")[0].innerHTML.substring(1)) > 0){
                startTaskTime = (new Date()).getTime();
                number ++;
                li[i].getElementsByTagName("a")[0].click();
                break;
            }
        }
    }

    if(document.location.href === "http:\/\/pphongbao.com/" || document.location.href === "http:\/\/pphongbao.com/#"){
        var t = "/index.php/fasttask/index?timestamp=" +new Date().getTime();
        mainView.router.reloadPage(t);
    }


},1000 * 15);


window.setInterval(function(){
    if(document.location.href.indexOf("detail?fast_id") !== -1){
        var minTime = parseInt(document.getElementById("fastdetail-container").getAttribute("data-remainmins"));
        if((new Date()).getTime() - startTaskTime  > minTime * 60 * 1000 + 15 * 1000 ){
            $$("#submit-button").click();
        }
    }
},1000 * 15);

window.setInterval(function(){
    try {
        if (document.location.href.indexOf("user/bindPhone") !== -1) {
            var u = proxyhost + "/sms/getHm";
            $$.ajax({
                method: "GET",
                url: u,
                timeout: 3e3,
                async: !1,
                dataType: "json",
                success: function (o) {
                    document.getElementsByName("phone")[0].value = o;
                    document.getElementsByClassName("button")[0].click();
                    sleep(2);
                    var u = proxyhost + "/sms/getYzm/" + o;
                    $$.ajax({
                        method: "GET",
                        url: u,
                        timeout: 0,
                        dataType: "json",
                        success: function (o) {
                            document.getElementsByName("code")[0].value = o;
                            sleep(2);
                            document.getElementsByClassName("button button-round active pp-button")[0].click();
                        }
                    })
                }
            });
        }
    }catch (error){

    }
},1000 * 60);

window.setInterval(function () {
    if (document.location.href.indexOf("user/modifyPwd") !== -1) {
        var pwd = randomString(6);
        document.getElementsByName("pwd")[0].value = pwd;
        document.getElementsByName("repwd")[0].value = pwd;
        sleep(2);
        document.getElementsByClassName("button button-round active pp-button")[0].click();
    }

}, 1000 * 15);


function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
    var maxPos = $chars.length;
    var pwd = '';
    for (var i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}

function sleep(millsec) {
    var now = new Date();
    var exitTime = now.getTime() + 1000 * millsec;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime) {
            break;
        }
    }
}
