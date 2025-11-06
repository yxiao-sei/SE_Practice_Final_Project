 助研系统接口文档

## 一、接口统一规约
在前后端分离的程序架构下，前后端交互一般是由前端通过`HTTP协议`访问后端提供的`Restful`风格的`API`进行的。在这种场景下，常用的`HTTP方法`主要包括：`GET`、`PUT`、`POST`和`DELETE`。
### 1. GET
`GET`方法用于获取资源详情或者列表，不对资源做变更。其一般使用场景如下：
1. 获取某个资源的详情：`/resourceName/{id}`，`Path Variable`中的`id`即为要获取的那个资源的唯一`id`

   特别的，当要获取当前登录用户的详情时，由于后端的`Security Context`中知道当前用户是谁，所以一般直接使用：`/user` 即可。

2. 分页获取某种资源列表：`/resourceName/list?page=xxx&size=xxx`

   `page`指当前的页码，`size`指每一页的资源数量，同样的，可以增加其他`Query String`参数以实现例如下拉筛选和模糊查找等功能。

### 2. PUT
`PUT`方法用于新增（添加）某个资源。新增的资源数据一般放在`RequestBody`中提交给后端。

### 3. POST
`POST`方法用于对某个资源进行修改（更新）。需要更新的数据一般放在`RequestBody`中提交给后端。

### 4. DELETE
`DELETE`方法用于删除某个资源。一般用法是：`/resourceName/{id}`，同样的，`Path Variable`中的`id`即为要删除的那个资源的唯一`id`。


## 二、活动接口定义


### 1. 条件搜索活动
    接口地址：`/sca/activity/list`

    方法：`GET`
    
    参数：activityName、activityState、hostDepartmentCode、applyTime、isAudit（true和false）
          conductTimeBegin（时间戳、举办起始时间）、conductTimeEnd（时间戳、举办截止时间）

`response-body`
```json
{
    "code": 0,
    "message": "OK",
    "result": {
           "id": "索引号",
           "createTime": "创建时间",
           "lastUpdateTime": "最近更新时间",
           "lastUpdater": "最近一次更新操作人",
           "activityName": "活动名称",
           "activityAddress": "活动地址",
           "activityNum": "活动人数",
           "studentTypeScope": "学历层次范围",
           "gradeScope": "年级范围",
           "departmentScope": "管理院系范围",
           "zyScope": "专业范围",
           "activityBrief": "活动简介",
           "hostDepartmentCode": "主办院系码",
           "hostDepartmentName": "主办院系",
           "activityState": "活动状态",
           "applyTime": "申请时间",
           "conductTimeBegin": "举办起始时间",
           "conductTimeEnd": "举办截止时间",
           "mediaId": "媒体文件id",
            "applyForm": "申请形式：开放报名、系统备案",
            "rejectReason": "驳回理由",
            "deptApprover": "院系审批人（保存姓名）",
            "xgbApprover": "校级审批人（保存姓名）"
    }
}
```

## 三、项目接口定义


### 1. 项目列表查询
    接口地址：/sca/activitySeries/list/参数
    方法： GET
    参数列表："ProjectName"
             "ProjectCode"
             "description"
             "hostDepartmentCode"
             "hostDepartmentName"
    
    返回结果："code": 0,
             "message": "OK",
             "result":

    调取示例：http://127.0.0.1:8080/sca/activitySeries/list?ProjectName=test1
    {
        "code": 0,
        "message": "OK",
        "result": [
            {
            "id": "4028818b790c08e601790c0b0d090001",
            "createdTime": 1619404721414,
            "creator": "20171133",
            "creatorName": "肖宇",
            "lastUpdateTime": 1619404721414,
            "lastUpdater": null,
            "name": "test1",
            "code": "test2",
            "description": "test3",
            "hostDepartmentCode": "",
            "hostDepartmentName": ""
            }
        ],
        "count": 1,
        "next": null
    }


### 2. 项目添加（不同名）
    接口地址：/sca/activitySeries

    方法： PUT
    参数：
                "name":项目名
                "code":项目编号
                "description":描述
    返回结果：有同名项目
    {
        "code": -1,
        "message": "有同名Project创建失败",
        "result": null
    }
             无同名项目
    {
        "code": 0,
        "message": "OK",
        "result":
    }

    调取示例：http://127.0.0.1:8080/sca/activitySeries
    BODY:   {
                "name": "test3",
                "code": "test2",
                "description": "test3"
            }
    {
        "code": 0,
        "message": "OK",
         "result": {
            "id": "4028818b790c08e601790c1588590004",
            "createdTime": 1619405408342,
            "creator": "20171133",
            "creatorName": "肖宇",
            "lastUpdateTime": 1619405408342,
            "lastUpdater": null,
            "name": "test3",
            "code": "test2",
            "description": "test3",
            "hostDepartmentCode": "",
            "hostDepartmentName": ""
        }
    }
### 3. 项目删除
    接口地址：/sca/activitySeries/id
    方法： DELETE
    参数：ID
    返回结果：删除成功
    {
        "code": 0,
        "message": "OK",
        "result": "ok"
    }
            没有找到ID
    {
        "code": -1,
        "message": "data activity not found by id: 4028818b790c08e601790c158859000",
        "result": null
    }
    调取示例：http://127.0.0.1:8080/sca/activitySeries/4028818b790c08e601790c1588590004
    {
        "code": 0,
        "message": "OK",
        "result": "ok"
    }
### 3. 项目搜索
    接口地址：/sca/activitySeries/id
    方法： GET
    参数：ID
    返回结果：没有找到Id
    {
        "code": -1,
        "message": "data activity not found by id: 4028818b790c08e601790c1588590004",
        "result": null
    }
             查找成功
    {
        "code": 0,
        "message": "OK",
        "result":
    }
    调取示例：http://127.0.0.1:8080/sca/activitySeries/4028818b790c08e601790c1b89da0005
    {
    "code": 0,
    "message": "OK",
    "result": {
        "id": "4028818b790c08e601790c1b89da0005",
        "createdTime": 1619405801941,
        "creator": "20180076",
        "creatorName": "顾春香",
        "lastUpdateTime": 1619405801941,
        "lastUpdater": null,
        "name": "德育教育",
        "code": "234123",
        "description": "德育教育德育教育德育教育德育教育德育教育",
        "hostDepartmentCode": "013902",
        "hostDepartmentName": "城市与区域科学学院"
        }
    }

### 4. 项目修改
    接口地址：/sca/activitySeries/id
    方法：POST
    参数：ID
    返回结果：没有修改项目名或修改项目名没有同名项目
    {
        "code": 0,
        "message": "修改成功",
        "result":
    }

             修改项目名但有同名项目
    {
        "code": -1,
        "message": "有同名Project修改失败",
        "result": null
    }
    调取示例：http://127.0.0.1:8080/sca/activitySeries/4028818b790c08e601790c2db9590007
            {
                "name": "1231231",
                "code": "test222",
                "description": "test1232313"
            }
    {
        "code": 0,
        "message": "修改成功",
        "result": {
            "id": "4028818b790c08e601790c2db9590007",
            "createdTime": 1619406993750,
            "creator": "20180076",
            "creatorName": "顾春香",
            "lastUpdateTime": 1619409701880,
            "lastUpdater": "20171133",
            "name": "1231231",
            "code": "test222",
            "description": "test1232313",
            "hostDepartmentCode": "013902",
            "hostDepartmentName": "城市与区域科学学院"
        }
    }