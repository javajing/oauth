### 1. 手机验证码登录

#### 1. 发送手机验证码
1. 路径: /api/captcha
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",    //客户端ID
        "mobile": "xxxxxxxxxxx"   //手机号码
    }
    ```
4. 响应格式：text  

| 状态码 | 响应体 | 备注 |  
| -----|----|----|
| 200 | {id} | 请求ID |
| 400 | 参数不合法 | client_id或者mobile错误 |
| 400 | 用户未注册 |  |
| 500 | 发送验证码失败 |  |

#### 2. 手机验证码登录
1. 路径: /api/token
2. 方式：post
3. 参数：
    ```json
    {
        "authorize_id": "xxx",       //第一步返回的请求ID
        "mobile": "xxxxxxxxxxx",     //手机号码
        "captcha": "xxx"             //手机验证码
    }
    ```
4. 响应格式：json   
    1. 状态码: 200  
    ```json
    {
        "access_token": "d0fdd664b95e4287a5594983c8e9c336",
        "refresh_token": "dd0393b12a674566ae75502bcc046513",
        "expires_in": 86400         //可配置
    }
    ```
    2. 状态码: 400  
    ```json
    {
        "error": "验证码已失效 or 手机号码不匹配 or 验证码错误 or 无效的客户端"
    }
    ```

### 3. 账号密码获取token
1. 路径: /token
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",       //客户端ID
        "grant_type": "password",    //固定值
        "username": "xxx",           //账号
        "password": "xxx"            //密码       
    }
    ```
4. 响应格式：json  
    1. 状态码: 200
    ```json
    {
        "access_token": "d0fdd664b95e4287a5594983c8e9c336",
        "refresh_token": "dd0393b12a674566ae75502bcc046513",
        "expires_in": 86400         //可配置
    }
    ```
    2. 状态码: 400
    ```json
    {
        "error": "无效的客户端 or 未授权的grantType or 账号和密码不能为空"
    }
    ```
    3. 状态码: 401
    ```json
    {
        "error": "账号或密码错误"
    }
    ```
    4. 状态码: 403
    ```json
    {
        "error": "账号被锁定，请申诉解锁"
    }
    ```
    5. 状态码: 406
    ```json
    {
        "error": "密码过期，必须修改密码"
    }
    ```

### 2. 用Token获取用户信息
1. 路径: /api/profile
2. 方式：get
3. 参数：
    1. 方式：Bearer Token
    2. 位置：HTTP HEAD
    4. 参数名：authorization
    3. 示例：authorization: "Bearer xxxxx-xxxx-xxxx-xxxxx"
4. 响应格式：json  
    1. 状态码: 200
    ```json
    {
        "id": "xxxxxx",
        "username": "xxxxx"     //非登录名，没有什么大的用处
    }
    ```
    2. 状态码: 403
    ```json
    {
        "error": "无效的access_token or 账号不存在"
    }
    ```

### 3. 刷新Token
1. 路径: /token
2. 方式：post
3. 参数：
    ```json
    {
        "client_id": "xxxxxx",                      //客户端ID
        "grant_type": "refresh_token",              //固定值
        "refresh_token": "xxxxx-xxxx-xxxx-xxxxx",   //refresh_token
    }
    ```
4. 响应格式：json  
    1. 状态码: 200
    ```json
    {
        "access_token": "d0fdd664b95e4287a5594983c8e9c336",     // 新值，刷新后的access_token，原值失效
        "refresh_token": "dd0393b12a674566ae75502bcc046513",    // 原值
        "expires_in": 86400                                     // 新值，可配置
    }
    ```
    2. 状态码: 400
    ```json
    {
        "error": "无效的客户端 or 未授权的grantType or refresh_token不能为空 or 无效的refresh_token"
    }
    ```