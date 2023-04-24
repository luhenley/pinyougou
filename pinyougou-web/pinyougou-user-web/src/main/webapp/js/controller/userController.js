/** 定义控制器层 */
app.controller('userController', function($scope, $timeout,$controller,baseService){

    /** 指定继承baseController */
    $controller('indexController',{$scope:$scope});

    /** 定义用户对象 */
    $scope.user = {};
    /** 定义保存用户方法 */
    $scope.save = function () {
        if (!$scope.password || $scope.user.password != $scope.password){
            alert("密码不一致，请重新输入！");
            return;
        }
        /** 发送异步Post请求*/
        baseService.sendPost("/user/save?smsCode="
            + $scope.smsCode, $scope.user)
            .then(function (response) {
                // 获取响应数据
                if (response.data){
                    alert("注册成功");
                    /** 注册成功后，清空文本框内容 */
                    $scope.user = {};
                    $scope.password = "";
                    $scope.smsCode = "";
                }else {
                    alert("注册失败");
                }
            });
    };

    // 定义变量
    $scope.tip="获取短信验证码";
    //禁用按钮标记
    $scope.disabled = false;

    /** 定义发送短信验证码方法 */
    $scope.sendCode = function () {
        var msg = "";
        // 判断手机号码
        if (!$scope.user.phone) {
            msg = "手机号码不能为空！";
        }else if(!/^1[3|5|6|7|8|9]\d{9}$/.test($scope.user.phone)){
            msg = "手机号码格式不正确！";
        }
        if (msg != ""){
            alert(msg);
        }else {
            baseService.sendGet("user/sendCode?phone="
                + $scope.user.phone)
                .then(function (response) {
                    //alert(response.data ? "发送成功！" : "发送失败！");
                    //获取响应数据
                    if (response.data) {
                        //开启倒计时
                        //调用倒计时方法
                        $scope.downCount(90);
                    } else {
                        alert("发送失败！");
                    }
                });
        }
    };


    //倒计时
    $scope.downCount = function (senconds) {
        $scope.disabled = true;
        senconds -= 1;
        if (senconds >= 0){
            $timeout(function(){
                //alert(senconds)
                $scope.downCount(senconds);
            },1000);
            $scope.tip = senconds + "S，重新获取！";
        }else {
            $scope.disabled = false;
            $scope.tip = "获取短信验证码";
        }
    };





    /**上传图片 */
    $scope.uploadFile = function () {
        baseService.uploadFile().then(function (response) {
            /** 如果上传成功，取出url */
            if(response.data.status == 200){
                /** 设置用户头像图片访问地址 */
                $scope.entity.headPic = response.data.url;
                //保存图片
                baseService.sendPost("user/saveInfo",$scope.entity)
                    .then(function (response) {
                    if (response.data){
                        window.location.reload();
                    }else {
                        alert("保存失败");
                    }
                });
            }else {
                alert("上传失败！")
            }
        });
    };


    //显示用户信息
    $scope.showInfo = function () {
        baseService.sendPost("/user/showInfo")
            .then(function (response) {
                $scope.entity = response.data;
                //将"address":"{"provinceId":"440000","cityId":"440900","areaId":"440983"}"字符串转换JSON
                $scope.region = JSON.parse($scope.entity.address);

                //用户手机号码改绑需要用，变量用错了，牵一发而动全身，不想改了，凑合着用吧
                $scope.user = response.data;
            });
    };

    // 保存用户信息
    $scope.saveInfo = function () {
        baseService.sendPost("/user/saveInfo",$scope.entity)
            .then(function (response) {
                if(response.data){
                    alert("保存成功");
                }else {
                    alert("保存失败");
                }
            });
    };


    /** 查询所有省份 */
    $scope.findAllProvince = function () {
        baseService.sendGet("/region/findAllProvince")
            .then(function (response) {
                $scope.provinces = response.data;
            });
    };
    /** 根据省份ID查询城市 */
    $scope.findRegionByProvinceId = function (provinceId) {
        baseService.sendGet("/region/findRegionByProvinceId",
            "provinceId=" + provinceId).then(function (response) {
            $scope.citys = response.data;
        });
    };
    /** 根据城市ID查询区域 */
    $scope.findRegionByCityId = function (cityId) {
        baseService.sendGet("/region/findRegionByCityId",
            "cityId=" + cityId).then(function (response) {
            $scope.areas = response.data;
        });
    };

    /** 监控 address.provinceId 变量，查询城市 */
    $scope.$watch('region.provinceId',function (newValue,oldValue) {
        if(newValue){
            $scope.findRegionByProvinceId(newValue);
        }else {
            $scope.citys = [];
        }
    });
    /** 监控 address.cityId 变量，查询区域 */
    $scope.$watch('region.cityId',function (newValue,oldValue) {
        if(newValue){
            $scope.findRegionByCityId(newValue);
        }else {
            $scope.ares = [];
        }
    });



    // 显示地址
    $scope.showAddress = function () {
        baseService.sendPost("/address/show")
            .then(function (response) {
                $scope.addressList = response.data;
            });
    }

    /** 监控 address.cityId 变量，查询区域 */
    $scope.$watch('region.areaId',function (newValue,oldValue) {
        if(newValue){
            //更新entity.address的数据绑定
            $scope.entity.address = $scope.region;
        }else {
            $scope.ares = [];
        }
    });


    $scope.saveAddress = function () {

        var url = "saveAddress";
        if ($scope.address.id){
            url = "updateAddress";
        }

        /*region {"provinceId":"440000","cityId":"440300","areaId":"440306"}*/
        /** 保存之前先将，数据整合好*/
        $scope.address.provinceId = $scope.region.provinceId;
        $scope.address.cityId = $scope.region.cityId;
        $scope.address.townId = $scope.region.areaId;

        /** 发送post请求 */
        baseService.sendPost("/address/" + url, $scope.address)
            .then(function(response){
                if (response.data){
                    /** 重新加载数据 */
                    window.location.reload();
                }else{
                    alert("操作失败！");
                }
            });
    };



    $scope.deleteAddress = function (id) {
        baseService.sendGet("/address/deleteAddressById",
            "id=" + id).then(function (response) {
                if (response.data){
                    window.location.reload();
                }else {
                    alert("操作失败！");
                }
            });
    }

    /** 显示地址修改 */
    $scope.show = function(item){
        /** 把json对象转化成一个新的json对象 */
        $scope.address = JSON.parse(JSON.stringify(item));
        $scope.region = {};
        $scope.region.provinceId = $scope.address.provinceId;
        $scope.region.cityId = $scope.address.cityId;
        $scope.region.areaId = $scope.address.townId;
    };


    /** 更改默认地址 */
    $scope.updateDefault = function (item) {

        baseService.sendPost("/address/updateDefault",item)
            .then(function (response) {
                if (response.data){
                    window.location.reload();
                }else {
                    alert("操作失败！");
                }
            });
    };


    /**  保存密码 */
    $scope.savePass = function () {
        /**  发送post请求 */
        baseService.sendPost("/user/updatePass", $scope.passData)
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
                        alert("操作成功");
                        //window.location.href = "/logout";
                    }else {
                        alert("密码错误！")
                    }
                }
            });
    };


    //显示用户手机号码和验证码信息
    $scope.showPhoneCode = function () {
                $scope.user = $scope.entity;
                //将"address":"{"provinceId":"440000","cityId":"440900","areaId":"440983"}"字符串转换JSON
                //$scope.region = JSON.parse($scope.entity.address);
                //将手机号码的中间四位隐藏
                $scope.phoneNumber = $scope.user.phone.substr(0,3) + "****" + $scope.user.phone.substr(7);


    };


    //手机绑定第一步
    $scope.nextStepOne = function () {
        baseService.sendPost("/verify/checkVerify?picCode="
            + $scope.picCode).then(function (response) {
                if (response.data){
                    baseService.sendPost("/user/nextStep?smsCode="
                        + $scope.smsCode,$scope.user).then(function (response) {
                        if (response.data){
                            /** 验证成功后，清空文本框内容 */
                            $scope.user = {};
                            $scope.picCode = "";
                            $scope.smsCode = "";
                            //跳转下一步
                            window.location.href = "/home-setting-address-phone.html";
                        }else {
                            alert("短信验证码错误");
                        }
                    });
                }else {
                    alert("图片验证码错误")
                }
        });
    };

    //手机绑定号码改绑
    $scope.nextStepTwo = function () {
        baseService.sendPost("/verify/checkVerify?picCode="
            + $scope.picCode).then(function (response) {
            if (response.data){
                /** 发送异步Post请求*/
                baseService.sendPost("/user/updatePhone?smsCode="
                    + $scope.smsCode, $scope.entity)
                    .then(function (response) {
                        // 获取响应数据
                        if (response.data){
                            /** 成功后，清空文本框内容 */
                            $scope.user = {};
                            $scope.picCode = "";
                            $scope.smsCode = "";
                            //跳转成功页面
                            window.location.href = "/home-setting-address-complete.html";
                        }else {
                            alert("操作失败");
                        }
                    });
            }else {
                alert("图片验证码错误")
            }
        });
    };


    // 显示用户的订单
    $scope.showOrders = function () {
        baseService.sendPost("/user/showOrders",$scope.entity)
            .then(function (response) {

        });
    };

});