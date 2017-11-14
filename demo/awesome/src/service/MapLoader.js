
class MapLoader {
    load() {
        return new Promise(function (resolve, reject) {
            window.initTheMap = function () {
                resolve(AMap)
            }

            var script = document.createElement('script')
            script.type = 'text/javascript'
            script.async = true
            script.src = 'https://webapi.amap.com/maps?v=1.4.0&key=4d015c2023dd28671fe6d997426d6f3f&plugin=AMap.Autocomplete'
            //script.onload = resolve
            script.onerror = reject
            document.head.appendChild(script);

            var script2 = document.createElement('script')
            script2.type = 'text/javascript'
            script2.async = true
            script2.src = 'https://cache.amap.com/lbs/static/addToolbar.js'
            //script.onload = resolve
            script.onerror = reject
            document.head.appendChild(script2);
        });
    }
}

export default MapLoader