var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator.throw(value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : new P(function (resolve) { resolve(result.value); }).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments)).next());
    });
};
/// <reference path="jquery.d.ts" />
/// <reference path="es6.d.ts" />
var Common;
(function (Common) {
    function getQueryString(key) {
        var regex_str = "^.+\\?.*?\\b" + key + "=(.*?)(?:(?=&)|$|#)";
        var regex = new RegExp(regex_str, "i");
        var url = window.location.toString();
        if (regex.test(url))
            return RegExp.$1;
        return "";
    }
    Common.getQueryString = getQueryString;
    function orderByID(data) {
        data.sort((a, b) => b.ID - a.ID);
        return data;
    }
    Common.orderByID = orderByID;
    function ajaxAsync(url = "", data, method = "GET", dataType = "json") {
        return __awaiter(this, void 0, void 0, function* () {
            return new Promise((resolve, reject) => {
                let ajaxSetting = {};
                ajaxSetting.url = url;
                ajaxSetting.type = method;
                ajaxSetting.dataType = dataType;
                ajaxSetting.data = data;
                ajaxSetting.success = (response) => {
                    resolve(response);
                };
                ajaxSetting.error = (xhr) => {
                    reject("请求出错，请联系网站管理员");
                };
                $.ajax(ajaxSetting);
            });
        });
    }
    Common.ajaxAsync = ajaxAsync;
    function queryString() {
        let urlItems = window.location.search.split("?");
        let values = new Map();
        if (urlItems.length == 1) {
            return values;
        }
        let params = urlItems[urlItems.length - 1];
        let result = params.split("&").reduce((rt, item) => {
            let [key, val] = item.split("=");
            rt.set(key, decodeURIComponent(val));
            return rt;
        }, values);
        return result;
    }
    Common.queryString = queryString;
    function mapToObject(map) {
        let result = {};
        for (let [key, val] of map.entries()) {
            result[key] = val;
        }
        return result;
    }
    Common.mapToObject = mapToObject;
})(Common || (Common = {}));
//验证登录状态
void function () {
    if (window.top["flag9527"]) {
        return;
    }
    $.get("/check_login", (response) => {
        window.top["flag9527"] = true;
        let data = eval("(" + response + ")");
        if (data.code != 0) {
            alert(data.message);
            window.location.href = "/login";
        }
    });
}();
//# sourceMappingURL=common.js.map