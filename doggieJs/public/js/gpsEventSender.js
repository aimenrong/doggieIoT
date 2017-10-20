 function onComplete(data) {
    var str=['定位成功'];
    str.push('经度：' + data.position.getLng());
    str.push('纬度：' + data.position.getLat());
    if(data.accuracy){
       str.push('精度：' + data.accuracy + ' 米');
    }
   str.push('是否经过偏移：' + (data.isConverted ? '是' : '否'));
   console.log(str);
}
function onError(data) {
    console.error('定位失败');
}

