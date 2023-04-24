/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope,$sce,$location,
$controller, baseService) {
    /** 指定继承baseController */
    $controller("baseController",{$scope:$scope});

    // 将文本转化成html
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    /** 定义搜索参数对象 */
    $scope.searchParam = {keywords : '', category : '',
        brand : '', price : '', spec : {},
        page : 1, rows : 10, sortField : '',sort : ''};

    /** 获取检索关键字 */
    $scope.getkeywords = function(){
        $scope.searchParam.keywords = $location.search().keywords;
        $scope.search();
    };

    /** 定义排序搜索方法 */
    $scope.sortSearch = function(sortField, sort){
        $scope.searchParam.sortField = sortField;
        $scope.searchParam.sort = sort;
        $scope.search();
    };

    /** 定义初始化页码方法 */
    var initPageNum = function () {
        /** 定义页码数组 */
        $scope.pageNums = [];
        /** 获取总页数 */
        var totalPages = $scope.resultMap.totalPages;
        /** 开始页码 */
        var firstPage = 1;
        /** 结束页码 */
        var lastPage = totalPages;

        /** 前面有点 */
        $scope.firstDot = true;
        /** 后面有点 */
        $scope.lastDot = true;

        /** 如果总页数大于5，显示部分页码 */
        if (totalPages > 5){
            // 如果当前页码处于前面位置
            if ($scope.searchParam.page <= 3){
                lastPage = 5; //生成前5页页码
                $scope.firstDot = false; // 前面没点
            }
            // 如果当前页码处于后面位置
            else if ($scope.searchParam.page >= totalPages - 3){
                firstPage = totalPages - 4; //生成后5页页码
                $scope.lastDot = false; // 后面没点
            }else{// 当前页码处于中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else {
            $scope.firstDot = false; // 前面没点
            $scope.lastDot = false; // 后面没点
        }
        /** 循环产生页码 */
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageNums.push(i);
        }

    };

    /** 根据分页搜索方法 */
    $scope.pageSearch = function (page) {
        page = parseInt(page);
        /** 页码验证 */
        if (page >= 1 && page <= $scope.resultMap.totalPages
            && page != $scope.searchParam.page){
            $scope.searchParam.page = page;
            $scope.search();
        }
    };

    /** 添加搜索选项方法 */
    $scope.addSearchItem = function (key,value) {
        /** 判断是商品分类、品牌、价格 */
        if(key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = value;
        } else {
            /** 规格选项 */
            $scope.searchParam.spec[key] = value;
        }
        /** 执行搜索结果 */
        $scope.search();
    };

    /** 删除搜索选项方法 */
    $scope.removeSearchItem = function(key){
        /** 判断是商品分类、品牌、价格 */
        if(key == "category" ||  key == "brand" || key == 'price'){
            $scope.searchParam[key] = "";
        }else{
            /** 删除规格选项 */
            delete $scope.searchParam.spec[key];
        }
        /** 执行搜索结果 */
        $scope.search();
    };

    /** 定义搜索方法 */
    $scope.search = function () {
        baseService.sendPost("/Search",$scope.searchParam)
            .then(function (response) {
                /** 获取搜索结果
                 * //获取响应数据{total：100, row:[{},{}]}
                 {"rows":[
                 {"brand":"苹果","category":"手机","goodsId":149187842867973,"id":1369324,
                 "image":"http://image.pinyougou.com/jd/wKgMg1qtKEOATL9nAAFti6upbx4132.jpg",
                 "price":6688.0,"seller":"品优购","specMap":{"网络":"联通4G","机身内存":"64G"},
                 "title":"Apple iPhone 8 Plus (A1864) 联通4G 64G","updateTime":1521297784000},
                 .............
                 {"brand":"苹果","category":"手机","goodsId":149187842867974,"id":1369333,
                 "image":"http://image.pinyougou.com/jd/wKgMg1qtMJiAQeytAAE05ISJidY664.jpg",
                 "price":9688.0,"seller":"品优购","specMap":{"网络":"移动4G","机身内存":"128G"},
                 "title":"Apple iPhone X (A1865) 移动4G 128G","updateTime":1521299765000}]}
                  */
                $scope.resultMap = response.data;
                /** 调用初始话页码方法 */
                initPageNum();
            });
    };
});
