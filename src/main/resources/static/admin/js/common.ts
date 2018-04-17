/// <reference path="jquery.d.ts" />
/// <reference path="es6.d.ts" />
namespace Common{
    export function getQueryString(key: string) : string  {
        var regex_str = "^.+\\?.*?\\b"+ key +"=(.*?)(?:(?=&)|$|#)"
        var regex = new RegExp(regex_str,"i");
        var url = window.location.toString();
        if(regex.test(url)) return RegExp.$1;
        return "";
    }

    export function  orderByID( data : any[] ) {
        data.sort( (a,b) => b.ID - a.ID );
        return data;
    }

    export async function  ajaxAsync(url = "", data:any, method = "GET", dataType = "json") {
        return new Promise<any>((resolve, reject) => {
            let ajaxSetting = {} as JQueryAjaxSettings;
            ajaxSetting.url = url;
            ajaxSetting.type = method;
            ajaxSetting.dataType = dataType;
            ajaxSetting.data = data;
            ajaxSetting.success = (response) => {
                resolve(response);
            }
            ajaxSetting.error = (xhr) => {
                reject("请求出错，请联系网站管理员");
            };
            $.ajax(ajaxSetting);
        });
    }


    export function queryString() : Map<string,string>{
        let urlItems = window.location.search.split("?");
        let values = new Map();
        if( urlItems.length == 1) {
            return values;
        }
        let params = urlItems[urlItems.length - 1];
        let result  = params.split("&").reduce((rt,item)=> {
            let [key,val] = item.split("=");
            rt.set(key, decodeURIComponent( val ) );
            return rt;
        },values);
        return result;
    }

    export function mapToObject( map: Map<string,any> ) {
        let result = {};
        for(let [key,val] of map.entries() ) {
            result[ key ] =  val;
        }
        return  result;
    }
}



//验证登录状态
void function () {
    if( window.top["flag9527"] ) {
        return;
    }
    $.get("/check_login", ( response )=>{
        window.top["flag9527"] = true;
        let data = eval("("+ response +")");
        if( data.code != 0 ) {
            alert( data.message )
            window.location.href = "/login";
        }
    })
}();
