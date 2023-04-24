/** 定义模块 */
var app = angular.module("pinyougou",[]);
/** 添加控制器 */
app.controller("sellerInfoController", function($scope, $http) {
    /** 读取列表数据绑定到表格中 */
    $scope.showInfo = function () {
        /** 发送异步请求查询数据 */
        $http.post("/seller/show").then(function (response) {
            $scope.entity = response.data;
        });
    };

    /** 保存商家信息 */
    $scope.saveInfo = function () {
        /**  发送post请求 */
        $http.post("/seller/update", $scope.entity)
            .then(function (response) {
                if (response.data) {
                    alert("保存成功！");
                } else {
                    alert("保存失败！");
                }
            });
    };

    /**  保存密码 */
    $scope.savePass = function () {
         /**  发送post请求 */
        $http.post("/seller/updatePass", $scope.passData)
            .then(function (response) {
                //密码验证
                var password=/(?!^[0-9]+$)(?!^[A-z]+$)(?!^[_*&^%$#@!?=+<>]+$)(?!^[^A-z0-9]+$)^.{6,16}$/;
                if ($scope.passData.oldPass == "") {
                    alert("旧密码不能为空！");
                }else if ($scope.passData.newPass == "") {
                    alert("新密码不能为空！");
                }else if ($scope.passData.reNewPass == "") {
                    alert("确认新密码不能为空！");
                }else if ($scope.passData.newPass != $scope.passData.reNewPass) {
                    alert("新密码必须一致！");
                } else{
                    if (response.data){
                        window.location.href = "/logout";
                    }else {
                        alert("密码错误！")
                    }
                }
            });
    };

});