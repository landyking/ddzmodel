##地主的确定分为三个环节：
1.随机指定从某人开始叫地主
2.叫地主
3.抢地主



收到协议:

叫地主阶段:

    如果:叫地主
            切换到抢地主状态
    如果:不叫
            移除该用户的叫地主机会
            检查:如果所有人都不叫地主,则退出,重新发牌,重新叫地主

抢地主阶段:

    如果:抢
            移除该用户的叫地主机会
    如果:不抢
            移除该用户的叫地主机会

检查:如果当前已经能确定地主身份
    切换到进行游戏状态
    退出

操作位置切换为下一个玩家

检查:如果当前玩家没有叫地主的机会,则默认该玩家执行不叫(抢)操作.
