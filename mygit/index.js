'use strict'
var request=require("request");
var cheerio =require("cheerio");
var iconv = require('iconv-lite');
var url1='https://www.naver.com';
var url2 ='http://www.snuco.com/html/restaurant/restaurant_menu1.asp'

function requestOptions(url) {
    return { method: "GET" ,uri: url ,
            headers: { "User-Agent": "Mozilla/5.0" } ,encoding: null };
        }
function message(string){ 
    return {
     "message": {
        "text": string
    },
    "keyboard": {
        "type": "buttons",
        "buttons": [
            "실시간 검색어", "식단정보"
        ]
    }
};}

exports.handler =function(event, context, callback) {
    
        var req = {
            "user_key": event["user_key"],
            "type": event["type"],
            "content": event["content"]
        };
if(req.content == '실시간 검색어')
{ request(requestOptions(url1),
    function(error, response, body)
    {
        if(error){throw error}
        
        var RealtimeKeyword=''; var i=1;
        const $ = cheerio.load(body);
        $('div.ah_roll_area.PM_CL_realtimeKeyword_rolling span.ah_k').each(function(index, ele){
           
            var a=$(this).text();
            RealtimeKeyword=RealtimeKeyword+i+'위 : '+a+'\n';
            i++;
         })

    callback(null, message(RealtimeKeyword))
    })

}
else if(req.content == '식단정보')
{
  request(requestOptions(url2),
        function(error, response, body)
        {
            var dishresult=''
            if(error){throw error}
            const $ = cheerio.load(iconv.decode(Buffer(body), "euckr"));
            for(var i=22; i<30; i++){
            var pag = $('table tbody tr').eq(i).each(function(i, tr){
                var menu = $(this).children();
                var row = {
                    "식당": menu.eq(0).text(),
                    "아침": menu.eq(2).text(),
                    "점심": menu.eq(4).text(),
                    "저녁": menu.eq(6).text()
                };
                dishresult=dishresult+'\n'+row['식당']+'\n'+row['아침']+'\n'+row['점심']+'\n'+row['저녁'];
            })
        }
        
        callback(null, message(dishresult))
            })

} 
}