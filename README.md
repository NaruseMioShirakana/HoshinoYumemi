# HoshiniYumemi
一个基于Mirai的功能性机器人

----
项目地址:[HoshiniYumemi](https://github.com/FujiwaraShirakana/HoshiniYumemi)\
下载地址:[HoshiniYumemiReleases](https://github.com/FujiwaraShirakana/HoshiniYumemi/releases/tag/0.0.1)
----
### 主要功能：
- 好感度 （<del>说是好感度其实是权限等级</del>，好感度为负值则会导致机器人不理你，这时只能通过签到慢慢回复）
- 金币系统 （由于API存在限额，此设定主要是为了防止群友滥用）
- 签到 （签到图像布局<del>抄袭</del>借鉴自真寻Bot）
- 聊天机器人 （不同好感设定有不同的回应词条，若数据中无词条则接入腾讯聊天机器人）
- 腾讯云机器翻译 （需要好感度等级不小于4，金币2*String.Length来使用，好感度等级不低于7级则免金币使用
- 腾讯云OCR （由于开通OCR需要企业认证（貌似），所以目前所有关于这一方面的函数都打了//)
- Saucenao搜图 （需要好感度等级不小于5，金币1000枚才可以使用，好感度等级不小于8级时免金币使用）
- Lolicon搜图 （需要好感度等级不低于4级，金币100*获取数量方可使用，好感度等级不低于7级则免金币使用）
- 黑名单 （将好感度设置为-800并将一切增加好感度的行为变为减少好感度）
- 礼物商店 （给Bot送礼物）
- （TODO）各种其他娱乐型BOT的功能，如小游戏等
----
### 功能详细介绍：
#### 一、好感度系统：
- 1、系统介绍


    好感度系统为一切功能的一个前置系统，之所以在前面将好感度称为权限等
    级，是因为好感度决定解锁的机器人功能。
    
    好感度的获取途径主要有：
    1、签到；2、和机器人聊天；3、给机器人送礼物；4、在好感度等级足够的
    情况下和机器人进行亲密行为（如贴贴，摸头，叫老婆等）；5、夸机器人；
    6、todo
    
    失去好感度的主要原因：
    1、辱骂机器人；2、在好感度不足的情况下进行亲密行为（如贴贴，摸头，
    叫老婆等）；3、被管理员针对；4、todo
    
    好感度上限：
    普通群员上限为800；管理员为900；在data\srh.shirakana.hoshino
    yumemi.plugin\HoshinoYumemiUser.yml中SpecialUser中指定的QQ
    用户为1000

    好感度指令：
    @机器人 好感（查看好感）
    /usrcmd(我的) kk(查看好感)（查看好感）
    /koukann <add/decrease> Member Amount（指定Member好感加减）
    /koukann <addall/decreaseall> Amount（所有群成员好感统一加减）
    /好感度 添加/减少/全体添加/全体减少 分别对应以上指令
- 2、好感度存储：


```
#### data\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemiKouKann.yml

# 好感度列表
KouKannList: 
  123456: 0.0
  1234567: 1000.0
  QQ号码: 好感度数值（Double）
```
----
#### 二、金币系统：
- 1、系统介绍


    目前金币系统仅作为限制功能使用的筹码，以后会出小游戏功能

    指令：
    @机器人 金币（查看金币）
    /usrcmd(我的) mon(查看金钱)（查看金币）
    /money <add/decrease> Member Amount（指定Member金币加减）
    /金钱 添加/减少 分别对应以上指令
- 2、金币存储

```
#### data\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemiMoney.yml

# 金钱列表
Money: 
  12345: 0.0
  123456: 0.0
  QQ号码：金币数量
```
----
#### 三、签到系统：
- 1、系统介绍：


    在群内输入签到，或者AT机器人输入签到，即可进行签到，签到会根据抽卡机制
    按照不同段位给予相应的随机好感度以及金币（配图）data\srh.shirakana
    .hoshinoyumemi.plugin\HoshinoYumemiUser.yml中bool列表为当日签到状态
    于每日0点重置
----
#### 四、聊天系统：
- 1、系统介绍：


    需要配置腾讯云APIID和APIKey，配置位置在config\srh.shirakana.hoshino
    yumemi.plugin\HoshinoYumemi_TencentCloudApiConfig.yml，一切的聊天
    内容均需要AT机器人触发（特殊动作请注意好感度，可能会扣好感）

    当机器人受到符合条件的MessageEvent后，优先从data\srh.shirakana.hoshi
    noyumemi.plugin\HoshinoYumemiReplyList.yml中读取回复信息，若该列表中
    无响应信息，则使用腾讯云聊天机器人（均为模糊匹配，腾讯云聊天机器人为腾讯
    云自然语言处理中的聊天机器人）

    聊天机器人指令：
    /DataList <replyadd/replyadd> inputMsg KoukannLevel outputMsg其
    中inputMsg为输入的信息，KoukannLevel为好感度等级(好感度/100).toInt
    outputMsg为回复信息（该条回复会被写入上面提到的文件中）
    /数据 添加回复/删除回复 分别对应以上指令

- 2、回复列表存储：

```
#### data\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemiUser.yml

# 回复列表
ReplyList: 
  1919810: 
    - 0: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 0: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 0: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 1: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 1: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 1: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 2: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 2: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 2: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 3: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 3: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 3: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 4: 请不要唐突恶臭！
    - 4: 为什么总是要这么臭呢？
    - 5: 请不要唐突恶臭！
    - 5: 为什么总是要这么臭呢？
    - 6: 请不要唐突恶臭！
    - 6: 为什么总是要这么臭呢？
    - 7: 臭死了
    - 7: 好臭啊
    - 8: 臭死了
    - 8: 好臭啊
    - 9: 如果是你的话，就算是恶臭的也可以接受
    - 9: 但是，我还是更喜欢不臭的你
    - 10: 如果是你的话，就算是恶臭的也可以接受
    - 10: 但是，我还是更喜欢不臭的你
  114514: 
    - 0: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 0: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 0: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 1: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 1: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 1: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 2: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 2: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 2: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 3: 像你这种又屑又臭的人，有在这个群存在的必要吗？！（恼）
    - 3: 像你这种又屑又臭的人，为什么还在这个群里呢？（疑惑）
    - 3: 像你这种又屑又臭的人，还是赶紧退群罢！（无慈悲）
    - 4: 请不要唐突恶臭！
    - 4: 为什么总是要这么臭呢？
    - 5: 请不要唐突恶臭！
    - 5: 为什么总是要这么臭呢？
    - 6: 请不要唐突恶臭！
    - 6: 为什么总是要这么臭呢？
    - 7: 臭死了
    - 7: 好臭啊
    - 8: 臭死了
    - 8: 好臭啊
    - 9: 如果是你的话，就算是恶臭的也可以接受
    - 9: 但是，我还是更喜欢不臭的你
    - 10: 如果是你的话，就算是恶臭的也可以接受
    - 10: 但是，我还是更喜欢不臭的你
    
文件结构为：
  inputMsg1
    - KoukannLevel1: outputMsg1
    - KoukannLevel1: outputMsg2
    - KoukannLevel2: outputMsg1
  inputMsg2
    - KoukannLevel1: outputMsg1
    - KoukannLevel2: outputMsg1
    - KoukannLevel2: outputMsg2
    
当同一个KoukannLevel有多个outputMsg时，随机选取其中一个
```
----
#### 五、各个API的使用：
- 配置文件：


    需要配置config\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemi
    _TencentCloudApiConfig.yml中ID和Key
    
    需要配置config\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemi
    SaucenaoApiConfig.yml中Key



- 指令：


    /TCAPI(腾讯云) MT(机器翻译) InputString TargetLang 
    其中InputString为要翻译的字符串，空格用下划线"_"代替，TargetLang为
    目标语言，需要使用简写，如中文“zh”；日语“jp”；英语“en”
    /eroImage(涩图) g(发几张) Tag Amount
    获取Amount张（小于等于5）lolicon涩图，不支持R18，发送为转发消息模式
    30秒撤回，Tag可使用rand来代表随机Tag
    /eroImage(涩图) s(搜索) Image
    使用Saucenao搜索指定图片
----
#### 六、礼物商店
- 指令：


    /usrcmd(我的) giftlist（列出礼物列表）
    /usrcmd(我的) gift ID（给机器人送指定ID的礼物）
    /DataList <shopadd/shopdel> Name Cost（加减名为Name售价Cost的商品）
    /我的 礼物列表/送礼物
    /数据 添加商品/删除商品
    机器人好感度获取为物品价格/1000
- 文件：


```
#### data\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemiShop

# 商店列表
Shop: 
  - 古河面包: 1000.0
  - 商品名: 售价
```
----
#### 七、其他指令
- 黑名单：/KouKann <bladd/blrem> Member（将指定群员添加/移除出黑名单>
- @机器人 睡吧（关闭插件功能）
- @机器人 起床吧（开启插件功能）
----
#### 八、打工和学位

- 介绍：


    打工功能，不过与其他娱乐机器人不同，想要打工，必须取得相应专业的学位
    而学位可以通过考试获得，考试的题目需机器人的管理者自行建库。
    指令：
    /AdmCmd addTest <Specialize> <Question> <Answer>
    #为Specialize专业添加Question问题，答案为Answer（专业不存在则创建）
    /AdmCmd addWork <requiredSpecialize> <reward>
    #添加requiredSpecialize专业对应的工作，基础工资为reward（需要存在
    专业“requiredSpecialize”（上一个指令创建）
    /AdmCmd delTest <Specialize> <Question>
    /AdmCmd delWork <requiredSpecialize>
    #下面两个为删除相应内容
    /usrcmd LS 
    #查看所有存在的专业
    /usrcmd mysp
    #查看自己的专业及学位
    /usrcmd giveupmysp
    #放弃自己的学位
    /usrcmd jointest <Specialize>
    #参加指定专业“Specialize”的考试
    /usrcmd exittest
    #退出当前考试
    /我的 参加考试/退出考试/有什么专业/我的专业/放弃学位
- 文件：


```
#### data\srh.shirakana.hoshinoyumemi.plugin\HoshinoYumemiCourse

# 题目
Course: 
数学:
  - 1+1=?: 2
  - 题目: 答案
专业:
  - 题目: 答案

还有两个json，存储用户和工作（）
```


#### 更新日志：
##### 6月10日-
    1、名称显示修复
    2、降低了聊天获取的好感度
    3、降低了签到获取的金币
    4、打工、学位系统实装并进入测试
##### 6月14日-
    1、标准化了试题的格式
    2、修复了考试的几个BUG
##### 6月29日-
    1、添加了中文指令
    2、腾讯云NLP聊天机器人开关（使用/AdmCmd Disable add/del 群号)
    群号为0时则操作机器人所在的所有群