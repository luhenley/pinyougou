// 定义商品详情的控制器
app.controller('itemController', function ($scope,$controller,$http) {
    /** 指定继承baseController */
    $controller("baseController",{$scope:$scope});

    // 定义默认的购买数量
    $scope.num = 1;

    // 购买数量加减操作
    $scope.addNum = function (x) {
        $scope.num = parseInt($scope.num);
        $scope.num += x;
    };

    $scope.$watch('num', function (newVal, oldVal) {
        // 正则表达式 判断是否为数字
        if (!/^\d+$/.test(newVal)){
            $scope.num = oldVal;
        }
        /** 购买数量不能小于1 */
        if ($scope.num <= 1){
            $scope.num = 1;
        }
    });

    /** 记录用户选择的规格选项 */
    $scope.specItems = {};
    /** 定义用户选择规格选项的方法 */
    $scope.selectSpec = function(name, value){
        $scope.specItems[name] = value;
        /** 查找对应的SKU商品 */
        searchSku();
    };
    /** 判断某个规格选项是否被选中 */
    $scope.isSelected = function(name, value){
        return $scope.specItems[name] == value;
    };

    /** 加载默认的SKU */
    $scope.loadSku = function(){
        /** 取第一个SKU */
        $scope.sku = itemList[0];
        //alert($scope.sku.spec);
        /** 获取SKU商品选择的选项规格 */
        $scope.specItems = JSON.parse($scope.sku.spec);
    };


    // 搜索用户选择的SKU
    $scope.searchSku = function () {
        // 迭代所有的SKU [{},{}]
        itemList.forEach(function (item) {
            // item.spec : 字符串
            // $scope.spec: json对象
            if (item.spec == JSON.stringify($scope.specItems)){
                $scope.sku = item;
                return;
            }
        });
    };

    // 加入购物车按钮事件绑定
    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addCart?itemId="
        + $scope.sku.id + "&num="
            + $scope.num,{"withCredentials":true})
            .then(function (response) {
                if (response.data){
                    /** 跳转到购物车页面 */
                    //location.href="http://cart.pinyougou.com/cart.html";
                    loacation.herf="http://cart.pinyougou.com";
                }else {
                    alert("请求失败！");
                }
            });
        //alert('skuid:' + $scope.sku.id);
    };

});
